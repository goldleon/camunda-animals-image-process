package com.bouchtaoui.camunda.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bouchtaoui.camunda.model.Picture;
import com.bouchtaoui.camunda.repository.PictureRepository;

import reactor.core.publisher.Mono;

@Service
public class PictureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PictureService.class);

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private ExternalApiService externalApiService;

    public Mono<Picture> fetchAndSavePicture(String animal, String jobId) {
        Mono<byte[]> pictureDataMono;

        switch (animal.toLowerCase()) {
            case "dog":
                pictureDataMono = externalApiService.fetchAnimalPicture(animal);
                LOGGER.info("##### API response for the animal = {} ", animal);
                break;
            case "bear":
                pictureDataMono = externalApiService.fetchAnimalPicture(animal);
                LOGGER.info("##### API response for the animal = {} ", animal);
                break;
            case "cat":
                pictureDataMono = externalApiService.fetchCatPicture();
                LOGGER.info("##### API response for the animal = {} ", animal);
                break;
            default:
                return Mono.error(new IllegalStateException(
                        new StringBuilder().append("Unknown animal: ").append(animal).toString()));
        }

        return pictureDataMono.flatMap(data -> {
            String base64Image = Base64.getEncoder().encodeToString(data);
            LOGGER.info("##### Image Data for the animal {} :::::: {} ", animal, base64Image);
            Picture picture = new Picture();
            picture.setAnimal(animal);
            picture.setImageData(base64Image);
            picture.setId(jobId);
            return pictureRepository.save(picture);
        });

    }

    public Mono<Picture> getPictureByJobId(String jobId) {
        return pictureRepository.findById(jobId);
    }

}
