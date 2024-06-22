package com.bouchtaoui.camunda.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bouchtaoui.camunda.model.Picture;

import reactor.core.publisher.Mono;

@Repository
public interface PictureRepository extends ReactiveMongoRepository<Picture, String> {
    Mono<Picture> findByJobId(String jobId);
}
