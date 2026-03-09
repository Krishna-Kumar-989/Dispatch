package com.clark.roper.Dispatch.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class GeneralLetterReplyResponse {
  private Long id;
  private Long authorId;
  private String authorUsername;
  private String content;
  private Instant createdAt;
}
