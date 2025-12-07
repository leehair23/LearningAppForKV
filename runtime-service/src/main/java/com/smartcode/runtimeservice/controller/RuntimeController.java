package com.smartcode.runtimeservice.controller;

import com.smartcode.runtimeservice.dto.CodeExecuteRequest;
import com.smartcode.runtimeservice.dto.CodeExecuteResponse;
import com.smartcode.runtimeservice.service.DockerSandboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runtime")
@RequiredArgsConstructor
public class RuntimeController {
    private final DockerSandboxService dockerSandboxService;
    @PostMapping("/execute")
    public ResponseEntity<CodeExecuteResponse> executeSync(@RequestBody CodeExecuteRequest req){
        return ResponseEntity.ok(dockerSandboxService.execute(req));
    }
}
