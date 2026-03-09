package com.clark.roper.Dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeneralLetterReplyRequest {

  @NotBlank(message = "Reply content is required")
  @Size(max = 5000, message = "Reply must be at most 5000 characters")
  private String content;
}
