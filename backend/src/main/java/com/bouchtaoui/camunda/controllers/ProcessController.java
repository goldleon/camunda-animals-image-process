package com.bouchtaoui.camunda.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bouchtaoui.camunda.model.ProcessInstanceResponse;
import com.bouchtaoui.camunda.service.ProcessService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @PostMapping("/start")
    public Mono<ResponseEntity<ProcessInstanceResponse>> startProcess(@RequestParam String animal) {
        return processService.startProcess(animal)
                .map(process -> ResponseEntity.status(HttpStatus.CREATED).body(process))
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }
}
