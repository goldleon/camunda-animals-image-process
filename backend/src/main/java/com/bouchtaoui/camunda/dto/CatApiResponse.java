package com.bouchtaoui.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CatApiResponse {
    private String id;
    private String url;
    private int width;
    private int height;
}
