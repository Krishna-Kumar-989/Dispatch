package com.clark.roper.Dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificLettersRequest {


    private Long receiverId;


    private String content;



}
