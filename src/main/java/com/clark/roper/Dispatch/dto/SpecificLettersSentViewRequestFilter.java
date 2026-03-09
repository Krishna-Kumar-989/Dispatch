package com.clark.roper.Dispatch.dto;

import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import lombok.Data;

import java.time.Instant;

@Data
public class SpecificLettersSentViewRequestFilter {

    private Long receiverId;

    private SpecificLettersStatusEnum status;

    private Instant createdAtLowerLimit;

    private Instant createdAtHigherLimit;

}
