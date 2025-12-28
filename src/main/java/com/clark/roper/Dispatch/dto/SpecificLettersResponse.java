package com.clark.roper.Dispatch.dto;

import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificLettersResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private SpecificLettersStatusEnum status;
    private Instant createdAt;

}
