package com.bouchtaoui.camunda.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bouchtaoui.camunda.model.Picture;

@Repository
public interface PictureRepository extends ReactiveMongoRepository<Picture, String> {
}
