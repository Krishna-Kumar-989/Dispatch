package com.clark.roper.Dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
