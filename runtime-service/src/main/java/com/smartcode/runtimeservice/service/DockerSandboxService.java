package com.smartcode.runtimeservice.service;

import com.smartcode.runtimeservice.dto.CodeExecuteRequest;
import com.smartcode.runtimeservice.dto.CodeExecuteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class DockerSandboxService {

    public CodeExecuteResponse execute(CodeExecuteRequest request) {
        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("job-" + request.getId());
            String filename = getFilename(request.getLanguage());
            Files.writeString(tmpDir.resolve(filename), request.getSourceCode());

            ProcessBuilder pb = buildDockerCommand(request.getLanguage(), tmpDir, filename);
            pb.redirectErrorStream(true);

            return runProcess(pb, request.getStdin());

        } catch (Exception e) {
            log.error("Execution Error: ", e);
            return CodeExecuteResponse.builder()
                    .status("ERROR")
                    .output("System Error: " + e.getMessage())
                    .exitCode(-1)
                    .build();
        } finally {
            // 4. Dọn dẹp file tạm
            if (tmpDir != null) {
                deleteDir(tmpDir);
            }
        }
    }

    private CodeExecuteResponse runProcess(ProcessBuilder pb, String stdin) throws Exception {
        Process proc = pb.start();

        if (stdin != null && !stdin.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()))) {
                writer.write(stdin);
                writer.flush();
            } catch (IOException ignored) {}
        }
        try { proc.getOutputStream().close(); } catch (IOException ignored) {}

        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<String> outputFuture = pool.submit(() -> {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        });

        boolean finished;
        String rawOutput = "";
        int exitCode = -1;
        boolean timedOut = false;

        try {
            // Chờ tối đa 15s (Hard limit của Java, phòng trường hợp lệnh timeout của Linux không chạy)
            finished = proc.waitFor(15, TimeUnit.SECONDS);

            if (!finished) {
                proc.destroyForcibly();
                timedOut = true;
                rawOutput = "TIMEOUT: process killed after 15s";
            } else {
                exitCode = proc.exitValue();
                rawOutput = outputFuture.get(2, TimeUnit.SECONDS);
            }
        } catch (TimeoutException | InterruptedException e) {
            proc.destroyForcibly();
            timedOut = true;
            rawOutput = "TIMEOUT: Error reading output";
        } finally {
            pool.shutdownNow();
        }

        ExecutionResult result = parseMetrics(rawOutput, exitCode, timedOut);

        return CodeExecuteResponse.builder()
                .status(determineStatus(result)) // DONE, TIMEOUT, ERROR
                .output(result.output)
                .exitCode(result.exitCode)
                .timeExec(result.timeMs)
                .memoryUsage(result.memoryKB)
                .build();
    }

    private String determineStatus(ExecutionResult res) {
        if (res.timedOut) return "TIMEOUT";
        if (res.exitCode != 0) return "ERROR";
        return "DONE";
    }

    private ExecutionResult parseMetrics(String rawOutput, int exitCode, boolean timedOut) {
        StringBuilder cleanOutput = new StringBuilder();
        long memoryKB = 0;
        long timeMs = 0;

        if (rawOutput != null) {
            String[] lines = rawOutput.split("\n");
            for (String line : lines) {
                if (line.trim().startsWith("METRICS:")) {
                    try {
                        // Format: METRICS:5000:0.02
                        String[] parts = line.trim().split(":");
                        if (parts.length >= 3) {
                            memoryKB = Long.parseLong(parts[1]); // KB
                            double timeSec = Double.parseDouble(parts[2]);
                            timeMs = (long) (timeSec * 1000); //milisec
                        }
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse metrics line: {}", line);
                    }
                } else {
                    cleanOutput.append(line).append("\n");
                }
            }
        }

        return new ExecutionResult(exitCode, cleanOutput.toString().trim(), timedOut, memoryKB, timeMs);
    }

    private String getFilename(String lang) {
        return switch (lang.toLowerCase()) {
            case "java" -> "Main.java";
            case "c" -> "main.c";
            case "cpp", "c++" -> "main.cpp";
            default -> "main.py";
        };
    }

    private ProcessBuilder buildDockerCommand(String language, Path mountDir, String filename) {
        String mountPath = toDockerMountPath(mountDir);
        String mountArg = mountPath + ":/sandbox";

        String lang = (language == null) ? "python" : language.toLowerCase();
        String image;
        String payload;

        // Định dạng output của time: METRICS:<MemoryKB>:<TimeSeconds>
        String timeCmd = "/usr/bin/time -f \"METRICS:%M:%e\" ";

        switch (lang) {
            case "java":
                image = "coderunner/java:17";
                payload = "javac " + filename + " 2>&1 && " +
                        "if ls /sandbox/*.class >/dev/null 2>&1; then " +
                        timeCmd + "java -cp /sandbox Main 2>&1; " +
                        "else echo NO_CLASS; exit 127; fi";
                break;

            case "c":
                image = "coderunner/gcc:12";
                payload = "gcc -std=c11 -O2 -Wall -Wextra " + filename + " -o /sandbox/app 2>&1 && " +
                        "if [ -f /sandbox/app ]; then " +
                        timeCmd + "timeout 10 /sandbox/app 2>&1; " +
                        "else echo NO_APP; exit 127; fi";
                break;

            case "cpp", "c++":
                image = "coderunner/gcc:12";
                payload = "g++ -std=c++17 -O2 -Wall -Wextra " + filename + " -o /sandbox/app 2>&1 && " +
                        "if [ -f /sandbox/app ]; then " +
                        timeCmd + "timeout 10 /sandbox/app 2>&1; " +
                        "else echo NO_APP; exit 127; fi";
                break;

            default: // Python
                image = "coderunner/python:3.11";
                payload = timeCmd + "python /sandbox/" + filename + " 2>&1";
                break;
        }

        try {
            autoBuildImageIfMissing(image, lang);
        } catch (Exception e) {
            log.warn("autoBuildImageIfMissing failed: {}", e.getMessage());
        }

        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");
        cmd.add("-i");
        cmd.add("--rm");

        // Security Flags
        cmd.add("--network");       cmd.add("none");
        cmd.add("--security-opt");  cmd.add("no-new-privileges:true");
        cmd.add("--cap-drop");      cmd.add("ALL");
        cmd.add("--read-only");
        cmd.add("--tmpfs");         cmd.add("/tmp:rw,size=64m");
        cmd.add("--memory");        cmd.add("128m");
        cmd.add("--cpus");          cmd.add("0.5");
        cmd.add("--pids-limit");    cmd.add("64");
        cmd.add("--user");          cmd.add("1000:1000");

        cmd.add("-v");              cmd.add(mountArg);
        cmd.add("-w");              cmd.add("/sandbox");

        cmd.add(image);
        cmd.add(payload);

        log.info("DOCKER CMD: {}", cmd);
        return new ProcessBuilder(cmd);
    }

    private String toDockerMountPath(Path p) {
        String path = p.toAbsolutePath().toString();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            path = path.replace('\\', '/');
            if (path.length() > 1 && path.charAt(1) == ':') {
                char drive = Character.toLowerCase(path.charAt(0));
                path = "/" + drive + path.substring(2);
            }
        }
        return path;
    }

    private void deleteDir(Path path) {
        try {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {}
    }

    private void autoBuildImageIfMissing(String image, String lang) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "image", "inspect", image);
            if (pb.start().waitFor() == 0) return;
        } catch (Exception ignored) {}

        String langDir = switch (lang) {
            case "java" -> "java";
            case "c", "cpp", "c++" -> "gcc";
            default -> "python";
        };
        String contextPath = System.getProperty("user.dir") + "/images/" + langDir;

        log.info("Building image {} from {}", image, contextPath);
        try {
            new ProcessBuilder("docker", "build", "-t", image, contextPath)
                    .redirectErrorStream(true).start().waitFor(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Build failed", e);
        }
    }

    // Inner Class
    private static class ExecutionResult {
        final int exitCode;
        final String output;
        final boolean timedOut;
        final long memoryKB;
        final long timeMs;

        ExecutionResult(int exitCode, String output, boolean timedOut, long memoryKB, long timeMs) {
            this.exitCode = exitCode;
            this.output = output;
            this.timedOut = timedOut;
            this.memoryKB = memoryKB;
            this.timeMs = timeMs;
        }
    }
}