package com.bouchtaoui.camunda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    @Value("${api.rest.dog}")
    private String dogAPI;

    @Value("${api.rest.bear}")
    private String bearAPI;

    private final WebClient webClient;

    /**
     * @param webClientBuilder
     */
    public ExternalApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * @return Mono<String>
     */
    public Mono<byte[]> fetchDogPicture() {
        return webClient.get().uri(dogAPI).retrieve().bodyToMono(byte[].class);
    }

    /**
     * @return Mono<String>
     */
    public Mono<byte[]> fetchBearPicture() {
        return webClient.get().uri(bearAPI).retrieve().bodyToMono(byte[].class);
    }
}
