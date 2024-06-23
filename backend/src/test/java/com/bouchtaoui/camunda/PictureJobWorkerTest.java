package com.bouchtaoui.camunda.worker;

import com.bouchtaoui.camunda.service.PictureService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PictureJobWorkerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PictureJobWorkerTest.class);

    @Mock
    private PictureService pictureService;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private PictureJobWorker pictureJobWorker;

    @Captor
    private ArgumentCaptor<String> animalCaptor;

    @Captor
    private ArgumentCaptor<String> jobIdCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleDogPictureJob() {
        when(activatedJob.getType()).thenReturn("fetchDogPicture");
        when(activatedJob.getProcessInstanceKey()).thenReturn(123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "dog"));
        when(pictureService.fetchAndSavePicture(anyString(), anyString())).thenReturn(Mono.empty());

        pictureJobWorker.handleDogPictureJob(activatedJob);

        verify(pictureService).fetchAndSavePicture(animalCaptor.capture(), jobIdCaptor.capture());
        verifyNoMoreInteractions(pictureService);

        assertEquals("dog", animalCaptor.getValue());
        assertEquals("123", jobIdCaptor.getValue());
    }

    @Test
    void testHandleBearPictureJob() {
        when(activatedJob.getType()).thenReturn("fetchBearPicture");
        when(activatedJob.getProcessInstanceKey()).thenReturn(123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "bear"));
        when(pictureService.fetchAndSavePicture(anyString(), anyString())).thenReturn(Mono.empty());

        pictureJobWorker.handleBearPictureJob(activatedJob);

        verify(pictureService).fetchAndSavePicture(animalCaptor.capture(), jobIdCaptor.capture());
        verifyNoMoreInteractions(pictureService);

        assertEquals("bear", animalCaptor.getValue());
        assertEquals("123", jobIdCaptor.getValue());
    }

    @Test
    void testHandleCatPictureJob() {
        when(activatedJob.getType()).thenReturn("fetchCatPicture");
        when(activatedJob.getProcessInstanceKey()).thenReturn(123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "cat"));
        when(pictureService.fetchAndSavePicture(anyString(), anyString())).thenReturn(Mono.empty());

        pictureJobWorker.handleCatPictureJob(activatedJob);

        verify(pictureService).fetchAndSavePicture(animalCaptor.capture(), jobIdCaptor.capture());
        verifyNoMoreInteractions(pictureService);

        assertEquals("cat", animalCaptor.getValue());
        assertEquals("123", jobIdCaptor.getValue());
    }

    @Test
    void testHandleJobLogsCorrectly() throws Exception {
        when(activatedJob.getType()).thenReturn("fetchDogPicture");
        when(activatedJob.getProcessInstanceKey()).thenReturn(123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "dog"));
        when(pictureService.fetchAndSavePicture(anyString(), anyString())).thenReturn(Mono.empty());

        Method handleJobMethod = PictureJobWorker.class.getDeclaredMethod("handleJob", ActivatedJob.class);
        handleJobMethod.setAccessible(true);
        handleJobMethod.invoke(pictureJobWorker, activatedJob);

        verify(pictureService).fetchAndSavePicture(animalCaptor.capture(), jobIdCaptor.capture());
        verifyNoMoreInteractions(pictureService);

        assertEquals("dog", animalCaptor.getValue());
        assertEquals("123", jobIdCaptor.getValue());
    }

    @Test
    void testHandleJobClientException() {
        when(activatedJob.getType()).thenReturn("fetchDogPicture");
        when(activatedJob.getProcessInstanceKey()).thenReturn(123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "dog"));
        doThrow(new RuntimeException("ClientException")).when(pictureService).fetchAndSavePicture(anyString(),
                anyString());

        try {
            pictureJobWorker.handleDogPictureJob(activatedJob);
        } catch (RuntimeException e) {
            // Exception is expected, verify the interactions
            LOGGER.error("ERROR: {}", e.getMessage());
        }

        verify(pictureService).fetchAndSavePicture(anyString(), anyString());
        verifyNoMoreInteractions(pictureService);
    }
}