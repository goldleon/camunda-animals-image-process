package com.bouchtaoui.camunda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bouchtaoui.camunda.model.ProcessInstanceResponse;

import io.camunda.zeebe.client.ZeebeClient;
import reactor.core.publisher.Mono;

@Service
public class ProcessService {

    @Autowired
    private ZeebeClient zeebeClient;

    public Mono<ProcessInstanceResponse> startProcess() {
        return Mono.fromCallable(() -> {
            var event = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("animal-picture-process")
                    .latestVersion()
                    .send()
                    .join();

            return new ProcessInstanceResponse(String.valueOf(event.getProcessInstanceKey()));
        });
    }
}
