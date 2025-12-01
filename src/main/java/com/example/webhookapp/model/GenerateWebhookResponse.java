package com.example.webhookapp.model;

import lombok.Data;

@Data
public class GenerateWebhookResponse {
    private String webhook;
    private String accessToken;
}