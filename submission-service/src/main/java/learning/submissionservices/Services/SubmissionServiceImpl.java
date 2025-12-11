package learning.submissionservices.Services;

import learning.submissionservices.config.RabbitConfig;
import learning.submissionservices.dto.ScoreUpdateRequest;
import learning.submissionservices.dto.SubmissionResponse;
import learning.submissionservices.dto.SubmissionbRequest;
import learning.submissionservices.dto.content.ProblemDTO;
import learning.submissionservices.dto.runtime.CodeExecuteRequest;
import learning.submissionservices.dto.runtime.CodeExecuteResponse;
import learning.submissionservices.entity.Submission;
import learning.submissionservices.repository.ISubmissionRepository;
import learning.submissionservices.repository.SubmissionService;
import learning.submissionservices.utils.ContentClient;
import learning.submissionservices.utils.RuntimeClient;
import learning.submissionservices.utils.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final ISubmissionRepository submissionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RuntimeClient  runtimeClient;
    private final ContentClient contentClient;
    private final UserClient userClient;

    @Value("${app.internal-secret}")
    private String internalSecret;
    @Override
    public SubmissionResponse create(String userId, SubmissionbRequest req) {
        if("TEST".equals(req.getMode())){
            CodeExecuteRequest job = CodeExecuteRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .language(req.getLanguage())
                    .sourceCode(req.getSourceCode())
                    .stdin(req.getStdin())
                    .timeLimit(5.0)
                    .build();

            CodeExecuteResponse result = runtimeClient.execute(job);
            return SubmissionResponse.builder()
                    .id(job.getId())
                    .status(result.getStatus())
                    .output(result.getOutput())
                    .timeExec(result.getTimeExec())
                    .memoryUsage(result.getMemoryUsage())
                    .createdAt(Instant.now().toString())
                    .build();
        }
        Submission sub = new Submission();
        sub.setId(UUID.randomUUID().toString());
        sub.setUserId(userId);
        sub.setProblemId(req.getProblemId());
        if (req.getContestId() != null && !req.getContestId().isEmpty()) {
            sub.setContestId(req.getContestId());
        }
        sub.setLanguage(req.getLanguage());
        sub.setSourceCode(req.getSourceCode());
        sub.setMode("SUBMIT");
        sub.setStatus("PENDING");
        submissionRepository.save(sub);

        ProblemDTO problem = contentClient.getProblem(req.getProblemId(), internalSecret);
        String inputData = "";
        if (problem.getTestCases() != null && !problem.getTestCases().isEmpty()) {
            inputData = problem.getTestCases().getFirst().getInput();
        } else {
            log.warn("Problem {} has no testcases!", req.getProblemId());
        }
        CodeExecuteRequest job = CodeExecuteRequest.builder()
                .id(sub.getId())
                .language(req.getLanguage())
                .sourceCode(req.getSourceCode())
                .stdin(inputData)
                .timeLimit(problem.getTimeLimit())
                .memoryLimit(problem.getMemoryLimit())
                .build();
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, job);
        log.info("Sent job {} to Queue", sub.getId());

        return mapToResponse(sub);
    }

    @Override
    public SubmissionResponse getById(String id) {
        Submission sub = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return mapToResponse(sub);
    }

    @Override
    public void processCallback(String id, CodeExecuteResponse result) {
        Submission sub = submissionRepository.findById(id).orElse(null);
        if (sub == null) return;

        sub.setOutput(result.getOutput());
        sub.setTimeExec(result.getTimeExec());
        sub.setMemoryUsage(result.getMemoryUsage());

        String workerStatus = result.getStatus();
        int exitCode = result.getExitCode();

        if ("TIMEOUT".equals(workerStatus)) {
            sub.setStatus("TIME_LIMIT_EXCEEDED");
        }
        else if ("ERROR".equals(workerStatus) || exitCode != 0) {
            sub.setStatus("RUNTIME_ERROR");
            if (sub.getOutput() == null || sub.getOutput().isEmpty()) {
                sub.setOutput("Process exited with code " + exitCode);
            }
        }
        else {
            judgeSubmission(sub, result.getOutput());
        }

        sub.setUpdatedAt(Instant.now());
        submissionRepository.save(sub);

        if ("ACCEPTED".equals(sub.getStatus())) {
            try {
                double points = 10.0;

                ScoreUpdateRequest scoreReq = ScoreUpdateRequest.builder()
                        .scoreToAdd(points)
                        .incrementSolved(true)
                        .contestId(sub.getContestId())
                        .build();

                userClient.updateScore(sub.getUserId(), scoreReq, internalSecret);
                log.info("Points added for userId {}, contestId {}", sub.getUserId(), sub.getContestId());

            } catch (Exception e) {
                log.error("Failed to update leaderboard for submission {}", id, e);
            }
        }
    }
    @Override
    @Async
    public void rejudgeProblem(String problemId){
        log.info("Rejudging problem {}", problemId);
        List<Submission> submissions = submissionRepository.findByProblemId(problemId);

        ProblemDTO problem = contentClient.getProblem(problemId, internalSecret);
        for(Submission sub : submissions){
            if("SUBMIT".equals(sub.getMode())){
                sub.setStatus("PENDING");
                sub.setOutput(null);
                sub.setUpdatedAt(Instant.now());
                submissionRepository.save(sub);

                CodeExecuteRequest job = CodeExecuteRequest.builder()
                        .id(sub.getId())
                        .language(sub.getLanguage())
                        .sourceCode(sub.getSourceCode())
                        .stdin(problem.getTestCases().getFirst().getInput())
                        .timeLimit(problem.getTimeLimit())
                        .memoryLimit(problem.getMemoryLimit())
                        .build();
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, job);
            }
        }
        log.info("Rejudge triggered for {} submissions", submissions.size());
    }
    @Override
    public Map<String, Object> getSubmissionStats(){
        long total = submissionRepository.count();
        long accepted = submissionRepository.countByStatus("ACCEPTED");

        double acRate = total == 0 ? 0 : (double) accepted / total * 100;

        return Map.of(
                "totalSubmissions", total,
                "acceptedSubmissions", accepted,
                "acRate", acRate
        );
    }
    private void judgeSubmission(Submission sub, String actualOutput) {
        if ("TEST".equals(sub.getMode())) {
            sub.setStatus("SUCCESS");
            return;
        }

        try {
            ProblemDTO problem = contentClient.getProblem(sub.getProblemId(), internalSecret);
            if (problem.getTestCases() == null || problem.getTestCases().isEmpty()) {
                sub.setStatus("System Error: No Testcases found");
                return;
            }

            String expectedOutput = problem.getTestCases().getFirst().getExpectedOutput();

            String normActual = normalize(actualOutput);
            String normExpected = normalize(expectedOutput);

            if (normActual.equals(normExpected)) {
                sub.setStatus("ACCEPTED");
            } else {
                sub.setStatus("WRONG_ANSWER");
            }

        } catch (Exception e) {
            log.error("Error judging submission " + sub.getId(), e);
            sub.setStatus("INTERNAL_ERROR");
        }
    }
    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replace("\r\n", "\n").replace("\r", "\n");
    }
    private SubmissionResponse mapToResponse(Submission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .status(s.getStatus())
                .output(s.getOutput())
                .timeExec(s.getTimeExec())
                .memoryUsage(s.getMemoryUsage())
                .createdAt(s.getCreatedAt().toString())
                .build();
    }
}
