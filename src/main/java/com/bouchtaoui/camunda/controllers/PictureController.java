package com.bouchtaoui.camunda.controllers;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bouchtaoui.camunda.service.PictureService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    @GetMapping(value = "/{jobId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<ResponseEntity<byte[]>> getPicture(@PathVariable String jobId) {
        return pictureService.getPictureByJobId(jobId)
                .map(picture -> {
                    byte[] imageBytes = Base64.getDecoder().decode(picture.getImageData());
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
