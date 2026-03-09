package com.clark.roper.Dispatch.dto;

import com.clark.roper.Dispatch.enums.UserGenderEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserProfileCreateRequest {

    @NotNull(message = "Gender is required")
    private UserGenderEnum gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Country is required")
    private String country;

    //Junction tables
    @NotEmpty(message = "At least one language is required")
    private Set<String> languages;

    @NotEmpty(message = "At least one interest is required")
    private Set<String> interests;
}
