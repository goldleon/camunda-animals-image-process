package com.bouchtaoui.camunda.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bouchtaoui.camunda.service.PictureService;

import io.camunda.zeebe.client.api.command.ClientException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;;

@Component
public class PictureJobWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PictureJobWorker.class);

    @Autowired
    private PictureService pictureService;

    @JobWorker(type = "fetchDogPicture", autoComplete = true)
    public void handleDogPictureJob(final ActivatedJob job) {
        try {
            handleJob(job);
        } catch (ClientException e) {
            LOGGER.error("### Could not complete job {}, error details: {}", job, e);
        }
    }

    @JobWorker(type = "fetchBearPicture", autoComplete = true)
    public void handleBearPictureJob(final ActivatedJob job) {
        try {
            handleJob(job);
        } catch (ClientException e) {
            LOGGER.error("### Could not complete job {}, error details: {}", job, e);
        }
    }

    @JobWorker(type = "fetchCatPicture", autoComplete = true)
    public void handleCatPictureJob(final ActivatedJob job) {
        try {
            handleJob(job);
        } catch (ClientException e) {
            LOGGER.error("### Could not complete job {}, error details: {}", job, e);
        }
    }

    private void handleJob(final ActivatedJob job) {
        LOGGER.info("Camunda worker type {} witch ID {} starting...", job.getType(), job.getProcessInstanceKey());
        var animal = (String) job.getVariablesAsMap().get("animal");
        var jobId = String.valueOf(job.getProcessInstanceKey());
        LOGGER.info("Camunda worker for the ", animal);
        pictureService.fetchAndSavePicture(animal, jobId).subscribe();

        LOGGER.info("Camunda worker type {} witch ID {} finished.", job.getType(), job.getProcessInstanceKey());
    }

}
