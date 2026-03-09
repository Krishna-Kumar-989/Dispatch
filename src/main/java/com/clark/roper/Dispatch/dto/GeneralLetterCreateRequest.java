package com.clark.roper.Dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class GeneralLetterCreateRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 200, message = "Title must be at most 200 characters")
  private String title;

  @NotBlank(message = "Content is required")
  @Size(max = 10000, message = "Content must be at most 10000 characters")
  private String content;

  // Draft support : if true, letter is saved as DRAFT
  private boolean draft = false;

  // Tags : optional list of tag names
  private Set<String> tags;

  // Scheduled : if set, letter publishes at this time
  private Instant scheduledAt;
}
