package com.Fitness.userService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message="  email IS REQUIRED")
    @Email(message="enter the valid mail")
    private String email;
    private String keycloakId;
    @NotBlank(message="enter the password")
    @Size(min=6,message="password of ATLEAST 6 character")
    private String password;
    private String firstName;
    private String lastName;
}
