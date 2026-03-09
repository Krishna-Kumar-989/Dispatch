package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.ReportCreateRequest;
import com.clark.roper.Dispatch.entity.Report;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.ReportStatusEnum;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.ReportRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReportService {

  private static final int MAX_PAGE_SIZE = 100;

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Transactional
  public String createReport(ReportCreateRequest request, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User reporter = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Report report = new Report();
    report.setReporter(reporter);
    report.setTargetType(request.getTargetType());
    report.setTargetId(request.getTargetId());
    report.setReason(request.getReason());
    report.setDescription(request.getDescription());
    reportRepository.save(report);

    return "Report submitted successfully";
  }

  // Admin methods
  public Page<Map<String, Object>> listReports(String status, int page, int size) {
    Page<Report> reports;
    if (status != null && !status.isBlank()) {
      ReportStatusEnum statusEnum = ReportStatusEnum.valueOf(status.toUpperCase());
      reports = reportRepository.findByStatus(statusEnum,
          PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").descending()));
    } else {
      reports = reportRepository.findAll(
          PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").descending()));
    }
    return reports.map(this::toMap);
  }

  @Transactional
  public String reviewReport(Long reportId, ReportStatusEnum newStatus) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
    report.setStatus(newStatus);
    report.setReviewedAt(Instant.now());
    reportRepository.save(report);
    return "Report status updated to " + newStatus.name();
  }

  private Map<String, Object> toMap(Report r) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", r.getId());
    map.put("reporterUsername", r.getReporter().getUsername());
    map.put("targetType", r.getTargetType());
    map.put("targetId", r.getTargetId());
    map.put("reason", r.getReason());
    map.put("description", r.getDescription());
    map.put("status", r.getStatus());
    map.put("createdAt", r.getCreatedAt());
    map.put("reviewedAt", r.getReviewedAt());
    return map;
  }
}
