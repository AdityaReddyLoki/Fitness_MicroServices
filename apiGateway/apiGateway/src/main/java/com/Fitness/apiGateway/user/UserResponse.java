package com.Fitness.apiGateway.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data  // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
public class UserResponse {

    private String id;
    private String keycloakId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
