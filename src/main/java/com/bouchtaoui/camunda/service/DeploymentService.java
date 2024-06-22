package com.bouchtaoui.camunda.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import reactor.core.publisher.Mono;

@Service
public class DeploymentService {
    @Autowired
    private ZeebeClient zeebeClient;

    @Value("${bpmn.resourcePath}")
    private String bpmnResourcePath;

    @Value("${bpmn.resourceName}")
    private String bpmnResourceName;

    public Mono<DeploymentEvent> deployProcess() throws IOException {

        var resource = new ClassPathResource(bpmnResourcePath);
        var path = resource.getFile().toPath();
        var bpmnProcess = new String(Files.readAllBytes(path));

        ZeebeFuture<DeploymentEvent> zeebeFuture = zeebeClient
                .newDeployResourceCommand()
                .addResourceStringUtf8(bpmnProcess, bpmnResourceName)
                .send();

        CompletableFuture<DeploymentEvent> completableFuture = zeebeFuture.toCompletableFuture();
        return Mono.fromFuture(completableFuture);
    }

}
