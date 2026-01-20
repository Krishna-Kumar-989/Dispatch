package com.clark.roper.Dispatch.dto;


import com.clark.roper.Dispatch.enums.UserGenderEnum;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserProfileCreateRequest {

    private UserGenderEnum gender;
    private LocalDate dateOfBirth;
    private String country;

    //Junction tables
    private Set<String> languages;
    private Set<String> interests;

}
