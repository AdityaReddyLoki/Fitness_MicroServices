package com.Fitness.aiService.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.build();
    }
    public String getAnswer(String question){
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",question)
                        })
                }
        );
        try {
            return webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            log.error("Gemini API returned HTTP status {}", status.value());
            throw new IllegalStateException(
                    "Gemini API returned HTTP status " + status.value());
        } catch (WebClientRequestException e) {
            log.error("Gemini API request failed (transport error)");
            throw new IllegalStateException("Gemini API request failed due to a transport error");
        }
    }

}
