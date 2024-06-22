package com.bouchtaoui.camunda.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bouchtaoui.camunda.model.ProcessInstanceResponse;

import io.camunda.zeebe.client.ZeebeClient;
import reactor.core.publisher.Mono;

@Service
public class ProcessService {

    @Autowired
    private ZeebeClient zeebeClient;

    public Mono<ProcessInstanceResponse> startProcess(String animal) {
        return Mono.fromCallable(() -> {
            Map<String, Object> variables = new HashMap<>();

            variables.put("animal", animal);
            var event = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("animal-picture-process")
                    .latestVersion()
                    .variables(variables).send().join();

            return new ProcessInstanceResponse(String.valueOf(event.getProcessInstanceKey()));
        });
    }
}
