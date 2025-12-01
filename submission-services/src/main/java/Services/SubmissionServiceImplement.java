package Services;

import config.RabbitConfig;
import dto.SubmissionDTO;
import entity.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import repository.ISubmissionRepository;
import repository.SubmissionService;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class ServiceImplement implements SubmissionService {
    private static final Logger log = LoggerFactory.getLogger(ServiceImplement.class);
    private static final int MAX_OUTPUT_LENGTH = 5000;

    private final ISubmissionRepository submissionRepository;
    private final AmqpTemplate amqpTemplate;


    public ServiceImplement(ISubmissionRepository submissionRepository, AmqpTemplate amqpTemplate) {
        this.submissionRepository = submissionRepository;
        this.amqpTemplate = amqpTemplate;
    }
    @Override
    public SubmissionDTO.Response create(SubmissionDTO.CreateRequest req){
        Submission sub = new Submission();
        sub.id = UUID.randomUUID().toString();
        sub.language = req.getLanguage();
        sub.sourceCode = req.getSourceCode();
        sub.status = "PENDING";
        sub.createdAt = Instant.now();
        sub.updatedAt = System.currentTimeMillis();

        if("TEST".equals(req.getMode())){
            sub.dataInput = (req.getStdin() == null || req.getStdin().isEmpty()) ? null : req.getStdin();
            sub.problemId = null;
        }
        else{
            sub.problemId = req.getProblemId();
            sub.dataInput = null;
        }
        submissionRepository.save(sub);
        try{
            Map<String, Object> workerPayload = Map.of(
                    "id", sub.id,
                    "language", sub.language,
                    "code", sub.sourceCode,
                    // Input gửi sang Worker: Nếu null thì gửi chuỗi rỗng để Worker không lỗi
                    "stdin", sub.dataInput == null ? "" : sub.dataInput
            );
            amqpTemplate.convertAndSend(RabbitConfig.QUEUE, workerPayload);
            log.info("Enqueued submission [{}] mode={} lang={}", sub.id, req.getMode(), sub.language);
        }catch (Exception e){
            log.error("Failed to send RabbitMQ for submission {}", sub.id, e);
            sub.status = "INTERNAL_ERROR";
            sub.error = "Queue Service Unavailable";
            submissionRepository.save(sub);
        }
        return mapToDTO(sub);
    }
    @Override
    public SubmissionDTO.Response getById(String id){
        Submission sub = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + id));
        return mapToDTO(sub);
    }
    @Override
    public void processCallback(String id, SubmissionDTO.WorkerCallBack data){
        Submission sub = submissionRepository.findById(id).orElse(null);
        if (sub == null) {
            log.warn("Callback received for non-existent submission: {}", id);
            return;
        }

        // 1. Cập nhật Metrics
        sub.timeExec = data.getTime();
        sub.memoryUsage = data.getMemory();

        // 2. Cập nhật Output (Cắt ngắn nếu quá dài)
        String rawOutput = data.getOutput();
        if (rawOutput != null && rawOutput.length() > MAX_OUTPUT_LENGTH) {
            sub.output = rawOutput.substring(0, MAX_OUTPUT_LENGTH) + "\n... [Output Truncated]";
        } else {
            sub.output = rawOutput;
        }
        String workerStatus = data.getStatus();
        if("DONE".equals(workerStatus)){
            sub.status = "SUCCESS";
        }else if("TIMEOUT".equals(workerStatus)){
            sub.status = "TIME_LIMIT_EXCEEDED";
        }else{
            sub.status = "RUNTIME_ERROR";
            sub.error = "Process exited with code " + data.getExitCode();
        }
        sub.updatedAt = System.currentTimeMillis();
        submissionRepository.save(sub);
        log.info("Updated submission [{}] result: {} ({}ms, {}KB)", id, sub.status, sub.timeExec, sub.memoryUsage);
    }
    private SubmissionDTO.Response mapToDTO(Submission s) {
        SubmissionDTO.Response res = new SubmissionDTO.Response();
        res.setId(s.id);
        res.setStatus(s.status);
        res.setOutput(s.output); // Output này đã được cắt ngắn hoặc null
        res.setTimeExec(s.timeExec);
        res.setMemoryUsage(s.memoryUsage);
        res.setCreatedAt(s.createdAt != null ? s.createdAt.toString() : null);
        return res;
    }
}
