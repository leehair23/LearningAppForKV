package com.smartcode.runtimeservice.listener;

import com.smartcode.runtimeservice.config.RabbitConfig;
import com.smartcode.runtimeservice.dto.CodeExecuteRequest;
import com.smartcode.runtimeservice.dto.CodeExecuteResponse;
import com.smartcode.runtimeservice.service.DockerSandboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobListener {
    private final DockerSandboxService dockerSandboxService;
    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String submissionServiceUrl;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveJob(CodeExecuteRequest req) {
        log.info("Received Job Request: " + req.getId());

        CodeExecuteResponse result = dockerSandboxService.execute(req);

        String callbackUrl = submissionServiceUrl + "/submissions/" + req.getId() + "/result";
        try{
            restTemplate.patchForObject(callbackUrl, result, Void.class);
            log.info("Callback success for job {}", req.getId());
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
