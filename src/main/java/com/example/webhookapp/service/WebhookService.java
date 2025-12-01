package com.example.webhookapp.service;

import com.example.webhookapp.model.*;
import com.example.webhookapp.util.SqlQueryProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final RestTemplate restTemplate;

    @PostConstruct
    public void execute() {
        try {
            // Step 1: Generate Webhook and Token
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            GenerateWebhookRequest request = new GenerateWebhookRequest(
                    "John Doe",
                    "REG12347",
                    "john@example.com"
            );

            ResponseEntity<GenerateWebhookResponse> response =
                    restTemplate.postForEntity(url, request, GenerateWebhookResponse.class);

            GenerateWebhookResponse data = response.getBody();

            log.info("Webhook URL: {}", data.getWebhook());
            log.info("Access Token: {}", data.getAccessToken());

            // Step 2: Pick SQL based on regNo
            int lastDigits = Integer.parseInt(request.getRegNo().substring(request.getRegNo().length() - 2));

            String finalQuery =
                    (lastDigits % 2 == 0)
                            ? SqlQueryProvider.getQuestion2Query()
                            : SqlQueryProvider.getQuestion1Query();

            // Step 3: Submit SQL to webhook URL
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", data.getAccessToken());

            SubmitSolutionRequest solutionRequest = new SubmitSolutionRequest(finalQuery);

            HttpEntity<SubmitSolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            ResponseEntity<String> result =
                    restTemplate.postForEntity(data.getWebhook(), entity, String.class);

            log.info("Submission Response: {}", result.getBody());

        } catch (Exception e) {
            log.error("Error occurred", e);
        }
    }
}
