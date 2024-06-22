package com.bouchtaoui.camunda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "pictures")
public class Picture {
    @Id
    private String id;
    private String animal;
    private String imageData;

    @Indexed(unique = true)
    private String jobId;
}
