package com.clark.roper.Dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserBioRequest {

    @NotBlank(message = "Bio cannot be empty")
    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;
}
