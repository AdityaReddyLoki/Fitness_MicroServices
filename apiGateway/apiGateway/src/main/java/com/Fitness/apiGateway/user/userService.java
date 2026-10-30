 package com.Fitness.apiGateway.user;

import com.Fitness.apiGateway.user.RegisterRequest;
import com.Fitness.apiGateway.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class userService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {

        log.info("Validating user: {}", userId);

        return userServiceWebClient.get()
                .uri("/api/user/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, ex -> {

                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    }

                    return Mono.error(ex);
                });
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {

        log.info("Registering user: {}", request.getEmail());

        return userServiceWebClient.post()
                .uri("/api/user/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .doOnError(error ->
                        log.error("User registration failed: {}",
                                error.getMessage()));
    }
}
