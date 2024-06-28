package com.bouchtaoui.camunda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bouchtaoui.camunda.model.Picture;
import com.bouchtaoui.camunda.repository.PictureRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PictureServiceTest {

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private ExternalApiService externalApiService;

    @InjectMocks
    private PictureService pictureService;

    private String jobId;
    private String animal;
    private byte[] pictureData;

    @BeforeEach
    public void setUp() {
        jobId = "12345";
        animal = "dog";
        pictureData = new byte[] { 1, 2, 3, 4, 5 };
    }

    @Test
    public void testFetchAndSavePicture_Dog() {
        when(externalApiService.fetchAnimalPicture(animal)).thenReturn(Mono.just(pictureData));
        when(pictureRepository.save(any(Picture.class))).thenAnswer(i -> Mono.just((Picture) i.getArguments()[0]));

        Mono<Picture> result = pictureService.fetchAndSavePicture(animal, jobId);

        StepVerifier.create(result)
                .assertNext(picture -> {
                    assertNotNull(picture);
                    assertEquals(animal, picture.getAnimal());
                    assertEquals(jobId, picture.getId());
                    assertEquals(Base64.getEncoder().encodeToString(pictureData), picture.getImageData());
                })
                .verifyComplete();
    }

    @Test
    public void testFetchAndSavePicture_Cat() {
        animal = "cat";
        when(externalApiService.fetchCatPicture()).thenReturn(Mono.just(pictureData));
        when(pictureRepository.save(any(Picture.class))).thenAnswer(i -> Mono.just((Picture) i.getArguments()[0]));

        Mono<Picture> result = pictureService.fetchAndSavePicture(animal, jobId);

        StepVerifier.create(result)
                .assertNext(picture -> {
                    assertNotNull(picture);
                    assertEquals(animal, picture.getAnimal());
                    assertEquals(jobId, picture.getId());
                    assertEquals(Base64.getEncoder().encodeToString(pictureData), picture.getImageData());
                })
                .verifyComplete();
    }

    @Test
    public void testFetchAndSavePicture_UnknownAnimal() {
        animal = "elephant";

        Mono<Picture> result = pictureService.fetchAndSavePicture(animal, jobId);

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    public void testGetPictureByJobId() {
        Picture picture = new Picture();
        picture.setAnimal(animal);
        picture.setId(jobId);
        picture.setImageData(Base64.getEncoder().encodeToString(pictureData));
        when(pictureRepository.findById(jobId)).thenReturn(Mono.just(picture));

        Mono<Picture> result = pictureService.getPictureByJobId(jobId);

        StepVerifier.create(result)
                .assertNext(foundPicture -> {
                    assertNotNull(foundPicture);
                    assertEquals(animal, foundPicture.getAnimal());
                    assertEquals(jobId, foundPicture.getId());
                    assertEquals(Base64.getEncoder().encodeToString(pictureData), foundPicture.getImageData());
                })
                .verifyComplete();
    }

    @Test
    public void testGetPictureByJobId_NotFound() {
        when(pictureRepository.findById(jobId)).thenReturn(Mono.empty());

        Mono<Picture> result = pictureService.getPictureByJobId(jobId);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}