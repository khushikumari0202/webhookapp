package com.example.webhookapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitSolutionRequest {
    private String finalQuery;
}