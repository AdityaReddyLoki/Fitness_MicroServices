package com.Fitness.activityService.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class userValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId){
        try {

            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/user/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("User not found "+userId);
            } else if (e.getStatusCode().value() == 400) {
                throw new RuntimeException("Invalid user Request: " + userId);
            }

        }
        return false;
    }
}
