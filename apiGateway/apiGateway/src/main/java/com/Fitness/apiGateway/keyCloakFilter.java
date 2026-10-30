package com.Fitness.apiGateway;
import com.Fitness.apiGateway.user.RegisterRequest;
import com.Fitness.apiGateway.user.userService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class keyCloakFilter implements WebFilter {

    private final userService userService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-ID");

        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegisterRequest registerRequest = getUserDetails(token);

        if (registerRequest == null
                || registerRequest.getKeycloakId() == null) {
            log.error("Failed to extract user details from token");
            return Mono.error(
                    new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.UNAUTHORIZED));
        }

        userId = registerRequest.getKeycloakId();

        String finalUserId = userId;

        return userService.validateUser(finalUserId)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.info(
                                "User already exists, skipping registration");
                        return Mono.<Void>empty();
                    }

                    log.info("Registering user from Keycloak");

                    return userService.registerUser(registerRequest)
                            .then();
                })
                .then(Mono.defer(() -> {
                    ServerHttpRequest mutatedRequest =
                            exchange.getRequest()
                                    .mutate()
                                    .headers(headers ->
                                            headers.set(
                                                    "X-User-ID",
                                                    finalUserId))
                                    .build();

                    ServerWebExchange mutatedExchange =
                            exchange.mutate()
                                    .request(mutatedRequest)
                                    .build();

                    return chain.filter(mutatedExchange);
                }))
                .doOnError(error ->
                        log.error(
                                "User synchronization failed: {}",
                                error.getMessage()));
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token
                    .replaceFirst("(?i)^Bearer\\s+", "")
                    .trim();

            SignedJWT signedJWT =
                    SignedJWT.parse(tokenWithoutBearer);

            JWTClaimsSet claims =
                    signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();

            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));

            // Temporary placeholder, not a production password strategy.
            request.setPassword("dummyPassword123");

            return request;

        } catch (Exception e) {
            log.error("Failed to parse JWT claims", e);
            return null;
        }
    }
}
