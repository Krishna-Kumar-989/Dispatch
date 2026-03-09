package com.clark.roper.Dispatch.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class GeneralLetterViewResponse {
  private Long id;
  private Long authorId;
  private String authorUsername;
  private String title;
  private String content;
  private String status;
  private int replyCount;
  private int likeCount;
  private Set<String> tags;
  private Instant createdAt;
}
