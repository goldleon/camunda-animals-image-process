package com.bouchtaoui.camunda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bouchtaoui.camunda.dto.CatApiResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalApiService.class);

    @Value("${api.rest.dog}")
    private String dogAPI;

    @Value("${api.rest.bear}")
    private String bearAPI;

    @Value("${api.rest.cat}")
    private String catAPI;

    private final WebClient webClient;

    /**
     * @param webClientBuilder
     */
    public ExternalApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * @return Mono<byte[]>
     */
    public Mono<byte[]> fetchPicture(String ApiUrl) {
        return webClient.get().uri(ApiUrl).retrieve().bodyToMono(byte[].class);
    }

    /**
     * @return Mono<byte[]>
     */
    public Mono<byte[]> fetchAnimalPicture(String animal, String... args) {
        switch (animal) {
            case "dog":
                return fetchPicture(dogAPI);
            case "bear":
                return fetchPicture(bearAPI);
            case "cat":
                if (args.length > 0)
                    return fetchPicture(args[0]);
            default:
                return Mono.empty();
        }
    }

    /**
     * @return Mono<String>
     */
    public Mono<byte[]> fetchCatPicture() {
        return webClient.get().uri(catAPI)
                .retrieve()
                .bodyToMono(CatApiResponse[].class)
                .flatMapMany(Flux::fromArray)
                .next()
                .flatMap(catApiResponse -> {
                    var imageUrl = catApiResponse.getUrl();
                    LOGGER.info("#### Cat Picture URL : {}", imageUrl);
                    return fetchAnimalPicture("cat", imageUrl);
                });
    }
}
