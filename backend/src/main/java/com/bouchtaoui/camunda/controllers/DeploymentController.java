package com.bouchtaoui.camunda.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bouchtaoui.camunda.service.DeploymentService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/deploy")
public class DeploymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> deployProcess() throws IOException {
        return deploymentService.deployProcess()
                .map(deploymentEvent -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("deploymentId", deploymentEvent.getKey());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .doOnError(e -> LOGGER.error("#### Deployment failed : {}", e.getMessage()))
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Deployment failed: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                })
                .onErrorResume(IOException.class, e -> {
                    LOGGER.error("#### Deployment failed due to IO Exception: {}", e.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Deployment failed due to IO Exception: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
}
