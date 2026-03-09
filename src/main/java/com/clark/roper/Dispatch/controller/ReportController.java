package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.ReportCreateRequest;
import com.clark.roper.Dispatch.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
public class ReportController {

  private final ReportService reportService;


  //For reporting

  @PostMapping
  public String createReport(@Valid @RequestBody ReportCreateRequest request,
      @RequestHeader("Authorization") String authHeader) {
    return reportService.createReport(request, authHeader);
  }
}
