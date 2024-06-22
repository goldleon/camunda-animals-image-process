package com.bouchtaoui.camunda.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProcessInstanceResponse {
    @NonNull
    private String id;
    private String pictureDataString;
}
