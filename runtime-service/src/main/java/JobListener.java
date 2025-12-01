import config.RunnerConfig;
import entity.CodeJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class JobListener {

    @Value("{api.base-url}")
    private String apiBaseUrl;
    private final RestTemplate rest;

    public JobListener(RestTemplate rest) {
        this.rest = rest;
    }

    private String callbackUrl() {
        String base = this.apiBaseUrl;
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        if (base.endsWith("/api/callback")) return base;
        return base + "/api/callback";
    }

    @RabbitListener(queues = RunnerConfig.QUEUE)
    public void receive(CodeJob job) {
        log.info("Received Job: {} lang={}", job.getId(), job.getLanguage());
        job.setStatus("RUNNING");

        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("codejob-" + job.getId());
            String filename = chooseFileName(job.getLanguage());
            Path source = tmpDir.resolve(filename);
            Files.writeString(source, job.getCode() == null ? "" : job.getCode());

            ProcessBuilder pb = buildDockerCommand(job.getLanguage(), tmpDir, filename);
            pb.redirectErrorStream(true);

            ExecutionResult res = runDockerProcess(pb, job.getStdin(), 15);

            job.setOutput(res.output);
            job.setExitCode(res.exitCode);
            job.setMemory(res.memoryKB);
            job.setTimeExec(res.timeMs);

            job.setStatus(determineStatusString(res.exitCode, res.timedOut, res.timeMs));

            log.info("Job {} finished. Status={} Exit={} Time={}ms Mem={}KB",
                    job.getId(), job.getStatus(), job.getExitCode(), res.timeMs, res.memoryKB);

        } catch (Exception ex) {
            log.error("Executor error for job {}: {}", job.getId(), ex.toString(), ex);
            job.setStatus("ERROR");
            job.setOutput("Executor error: " + ex.getMessage());
            job.setExitCode(-2);
        } finally {
            if (tmpDir != null) {
                try {
                    Files.walk(tmpDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (Exception ignore) {
                    log.warn("Failed to cleanup temp dir for job {}", job.getId(), ignore);
                }
            }
        }

        try {
            log.debug("POST -> {}", callbackUrl());
            rest.postForObject(callbackUrl(), job, Object.class);
        } catch (HttpClientErrorException he) {
            log.error("Callback HTTP error: status={} body={}", he.getStatusCode(), he.getResponseBodyAsString(), he);
        } catch (Exception e) {
            log.error("Failed to post callback for job {}: {}", job.getId(), e.getMessage(), e);
        }
    }

    private String determineStatusString(int exitCode, boolean timedOut, long timeMs) {
        if (timedOut || timeMs > 15000) return "TIMEOUT";
        if (exitCode != 0) return "ERROR";
        return "DONE";
    }

    private String chooseFileName(String language) {
        if (language == null) return "main.py";
        switch (language.toLowerCase()) {
            case "java": return "Main.java";
            case "c": return "main.c";
            case "cpp": case "c++": return "main.cpp";
            case "py": case "python": return "main.py";
            default: return "main.py";
        }
    }

    /**
     * Xây dựng lệnh Docker Run.
     */
    private ProcessBuilder buildDockerCommand(String language, Path mountDir, String filename) {
        String mountPath = toDockerMountPath(mountDir);
        String mountArg = mountPath + ":/sandbox";

        String lang = (language == null) ? "python" : language.toLowerCase();
        String image;
        String payload;

        // Định dạng output của time: METRICS:<MemoryKB>:<TimeSeconds>
        // %M = Max Resident Set Size (KB)
        // %e = Real time (seconds)
        String timeCmd = "/usr/bin/time -f \"METRICS:%M:%e\" ";

        switch (lang) {
            case "java":
                image = "coderunner/java:17";
                // Compile -> Check class -> Time & Run
                payload = "javac " + filename + " 2>&1; " +
                        "if ls /sandbox/*.class >/dev/null 2>&1; then " +
                        timeCmd + "java -cp /sandbox Main 2>&1; " +
                        "else echo NO_CLASS; exit 127; fi";
                break;

            case "c":
                image = "coderunner/gcc:12";
                // Compile -> Check exe -> Time & Timeout & Run
                payload = "gcc -std=c11 -O2 -Wall -Wextra " + filename + " -o /sandbox/app 2>&1; " +
                        "if [ -f /sandbox/app ]; then " +
                        timeCmd + "timeout 10 /sandbox/app 2>&1; " +
                        "else echo NO_APP; exit 127; fi";
                break;

            case "cpp":
            case "c++":
                image = "coderunner/gcc:12";
                // Compile -> Check exe -> Time & Timeout & Run
                payload = "g++ -std=c++17 -O2 -Wall -Wextra " + filename + " -o /sandbox/app 2>&1; " +
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
            log.warn("autoBuildImageIfMissing failed for {}: {}", image, e.getMessage());
        }

        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");
        cmd.add("-i");
        cmd.add("--rm");

        // --- Security Flags ---
        cmd.add("--network");       cmd.add("none");
        cmd.add("--security-opt");  cmd.add("no-new-privileges:true");
        cmd.add("--cap-drop");      cmd.add("ALL");
        cmd.add("--read-only");
        cmd.add("--tmpfs");         cmd.add("/tmp:rw,size=64m");
        cmd.add("--memory");        cmd.add("128m"); // limit 128mib ram
        cmd.add("--cpus");          cmd.add("0.5");  // limit cpu
        cmd.add("--pids-limit");    cmd.add("64");   // limit pid
        cmd.add("--user");          cmd.add("1000:1000");

        // Mount volume
        cmd.add("-v");              cmd.add(mountArg);
        cmd.add("-w");              cmd.add("/sandbox");

        // Command payload
        cmd.add(image);
        cmd.add(payload);

        log.info("DOCKER CMD: {}", String.join(" ", cmd));
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

    /**
     * Chạy Docker process, xử lý stdin và phân tích output để tách metrics.
     */
    private ExecutionResult runDockerProcess(ProcessBuilder pb, String stdin, long timeoutSeconds) throws Exception {
        Process proc = pb.start();

        if (stdin != null && !stdin.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()))) {
                writer.write(stdin);
                writer.flush();
            } catch (IOException e) {
                log.warn("Failed to write stdin to process: {}", e.getMessage());
            }
        }
        try { proc.getOutputStream().close(); } catch (IOException ignored) {}


        ExecutorService readerPool = Executors.newSingleThreadExecutor();
        Future<String> outputFuture = readerPool.submit(() -> {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        });

        boolean finished = proc.waitFor(timeoutSeconds, TimeUnit.SECONDS);

        String rawOutput;
        int exitCode;
        boolean timedOut = false;

        if (!finished) {
            timedOut = true;
            proc.destroyForcibly();
            exitCode = -1;
            rawOutput = "TIMEOUT: process killed after " + timeoutSeconds + "s";
        } else {
            exitCode = proc.exitValue();
            try {
                rawOutput = outputFuture.get(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                rawOutput = "Failed to read output: " + e.getMessage();
            }
        }
        readerPool.shutdownNow();

        return parseMetrics(rawOutput, exitCode, timedOut);
    }

    /**
     * helper function
     */
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
                            timeMs = (long) (timeSec * 1000); // Đổi sang mili giây
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

    // Class nội bộ để chứa kết quả chạy + metrics
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

    private void autoBuildImageIfMissing(String image, String lang) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "image", "inspect", image);
            Process p = pb.start();
            if (p.waitFor(5, TimeUnit.SECONDS) && p.exitValue() == 0) return;
        } catch (Exception ignored) {}

        String langDir = switch (lang) {
            case "java" -> "java";
            case "c", "cpp", "c++" -> "gcc";
            default -> "python";
        };
        String projectRoot = System.getProperty("user.dir");
        String contextPath = projectRoot + File.separator + "images" + File.separator + langDir;

        log.info("Building image {} from {}", image, contextPath);
        ProcessBuilder buildPb = new ProcessBuilder(
                "docker", "build", "-f", contextPath + File.separator + "Dockerfile", "-t", image, contextPath
        );
        buildPb.redirectErrorStream(true);
        try {
            Process buildProc = buildPb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(buildProc.getInputStream()))) {
                while(br.readLine() != null) {}
            }
            buildProc.waitFor(600, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to auto-build image {}", image, e);
        }
    }
}