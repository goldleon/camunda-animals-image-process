package com.bouchtaoui.camunda;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.protocol.Protocol.USER_TASK_JOB_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.bouchtaoui.camunda.CamundaApplicationTests;
import com.bouchtaoui.camunda.model.ProcessInstanceResponse;
import com.bouchtaoui.camunda.service.ProcessService;

class ProcessServiceTest extends CamundaApplicationTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceTest.class);

    private static final String PROCESS_ID = "animal-picture-process";
    private static final String STARTEVENT_ID = "StartEvent_1";
    private static final String EXCLUSIVE_GATEWAY_ID = "Gateway_AnimalType";
    private static final String USERTASK_ID = "Task_SelectAnimal";

    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private ZeebeTestEngine zeebeTestEngine;

    @Autowired
    private ProcessService processService;

    @Test
    void testProcessIsDeployedAndCanBeStarted() {
        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_ID)
                .latestVersion()
                .send()
                .join();

        assertThat(processInstance).isActive();
    }

    @Test
    void testStartProcess() {
        // given
        Map<String, Object> variables = Collections.singletonMap("animal", "dog");

        // when
        Mono<ProcessInstanceResponse> result = processService.startProcess();

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                            .bpmnProcessId(PROCESS_ID)
                            .latestVersion()
                            .send()
                            .join();

                    assertThat(processInstance);

                    assertThat(processInstance)
                            .hasPassedElement(STARTEVENT_ID)
                            .hasNotPassedElement(USERTASK_ID)
                            .isActive();

                    // complete user task
                    try {
                        waitForUserTaskAndComplete(USERTASK_ID, variables);
                        waitForUserTaskAndComplete(USERTASK_ID, Collections.singletonMap("approved", true));
                    } catch (InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                        Assertions.fail("Test failed due to exception: " + e.getMessage());
                    }

                    // then process is waiting for service task to be completed
                    assertThat(processInstance)
                            .hasPassedElementsInOrder(STARTEVENT_ID, USERTASK_ID)
                            .hasNotPassedElement(EXCLUSIVE_GATEWAY_ID)
                            .isActive();
                })
                .verifyComplete();
    }

    public void waitForUserTaskAndComplete(String userTaskId, Map<String, Object> variables)
            throws InterruptedException, TimeoutException {
        LOGGER.info("#### STARTING wait for {} with {}", userTaskId, variables);
        // Let the workflow engine do whatever it needs to do
        zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));

        // Now get all user tasks
        List<ActivatedJob> jobs = zeebe
                .newActivateJobsCommand()
                .jobType(USER_TASK_JOB_TYPE)
                .maxJobsToActivate(1)
                .workerName("waitForUserTaskAndComplete")
                .send()
                .join()
                .getJobs();
        LOGGER.info("#### LOGGIN JOBS {}", jobs);

        // Should be only one
        assertTrue(jobs.size() > 0, "Job for user task '" + userTaskId + "' does not exist");
        ActivatedJob userTaskJob = jobs.get(0);
        LOGGER.info("#### CURRENT TASK {}", userTaskJob.getType());
        // Make sure it is the right one
        if (userTaskId != null) {
            assertEquals(userTaskId, userTaskJob.getElementId());
        }

        // And complete it passing the variables
        if (variables != null && variables.size() > 0) {
            zeebe.newCompleteCommand(userTaskJob.getKey()).variables(variables).send().join();
        } else {
            zeebe.newCompleteCommand(userTaskJob.getKey()).send().join();
        }
    }

    private void completeServiceTask(final String jobType) throws InterruptedException, TimeoutException {
        completeServiceTasks(jobType, 2);
    }

    private void completeServiceTasks(final String jobType, final int count)
            throws InterruptedException, TimeoutException {

        final var activateJobsResponse = zeebe.newActivateJobsCommand().jobType(jobType).maxJobsToActivate(count).send()
                .join();

        final int activatedJobCount = activateJobsResponse.getJobs().size();
        if (activatedJobCount < count) {
            Assertions.fail(
                    "Unable to activate %d jobs, because only %d were activated."
                            .formatted(count, activatedJobCount));
        }

        for (int i = 0; i < count; i++) {
            final var job = activateJobsResponse.getJobs().get(i);

            zeebe.newCompleteCommand(job.getKey()).send().join();
        }

        zeebeTestEngine.waitForIdleState(Duration.ofSeconds(1));
    }
}