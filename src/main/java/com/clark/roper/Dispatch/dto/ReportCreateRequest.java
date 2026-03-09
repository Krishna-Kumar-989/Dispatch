package com.clark.roper.Dispatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ReportCreateRequest {

  @NotBlank(message = "Target type is required")
  @Size(max = 50, message = "Target type must be at most 50 characters")
  private String targetType;

  @NotNull(message = "Target ID is required")
  private Long targetId;

  @NotBlank(message = "Reason is required")
  @Size(max = 500, message = "Reason must be at most 500 characters")
  private String reason;

  @Size(max = 2000, message = "Description must be at most 2000 characters")
  private String description;
}
