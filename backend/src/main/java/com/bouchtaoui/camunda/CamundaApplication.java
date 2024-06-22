package com.bouchtaoui.camunda;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath:bpmn/animal-picture-process.bpmn")
public class CamundaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaApplication.class);

    public static void main(String[] args) {

        var app = new SpringApplication(CamundaApplication.class);
        var env = app.run(args).getEnvironment();

        var protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        var hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            LOGGER.warn("Impossible to determine the server's address, please use `localhost` to access the server");
        }

        LOGGER.info("\n----------------------------------------------------------\n\t" //
                + "Application '{}' Starting! Access URLs:\n\t" //
                + "Local: \t\t{}://localhost:{}\n\t" //
                + "External: \t{}://{}:{}\n\t" //
                + "Profile(s): \t{}" //
                + "\n----------------------------------------------------------", //
                env.getProperty("spring.application.name"), //
                protocol, env.getProperty("server.port"), //
                protocol, hostAddress, env.getProperty("server.port"), //
                env.getActiveProfiles());

    }

}