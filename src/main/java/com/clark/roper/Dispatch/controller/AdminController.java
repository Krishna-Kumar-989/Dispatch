package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.service.AdminService;
import com.clark.roper.Dispatch.service.AuditService;
import com.clark.roper.Dispatch.service.ReportService;
import com.clark.roper.Dispatch.service.SeedService;
import com.clark.roper.Dispatch.enums.ReportStatusEnum;
import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

  private final AdminService adminService;
  private final ReportService reportService;
  private final AuditService auditService;
  private final SeedService seedService;


  //User Management :

  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public Page<AdminService.AdminUserView> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return adminService.getAllUsers(page, Math.min(size, 100));
  }

  @PatchMapping("/users/{id}/role")
  @PreAuthorize("hasRole('ADMIN')")
  public String changeUserRole(@PathVariable("id") Long userId,
      @RequestParam UserRolesEnum role) {
    return adminService.changeUserRole(userId, role);
  }

  @PatchMapping("/users/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public String changeUserStatus(@PathVariable("id") Long userId,
      @RequestParam UserStatus status) {
    return adminService.changeUserStatus(userId, status);
  }



  //Report Management :

  @GetMapping("/reports")
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Map<String, Object>> getReports(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return reportService.listReports(status, page, size);
  }

  @PatchMapping("/reports/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public String reviewReport(@PathVariable("id") Long reportId,
      @RequestParam ReportStatusEnum status) {
    return reportService.reviewReport(reportId, status);
  }

  //Audit Logs :

  @GetMapping("/audit")
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Map<String, Object>> getAuditLogs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return auditService.getLogs(page, size);
  }

  @GetMapping("/audit/user/{username}")
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Map<String, Object>> getAuditLogsByUser(
      @PathVariable("username") String username,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return auditService.getLogsByUser(username, page, size);
  }

  //Seed Data Management :

  // Languages
  @GetMapping("/seed/languages")
  @PreAuthorize("hasRole('ADMIN')")
  public List<String> getAllLanguages() {
    return seedService.getAllLanguages();
  }

  @PostMapping("/seed/languages")
  @PreAuthorize("hasRole('ADMIN')")
  public String addLanguage(@RequestParam @NotBlank @Size(max = 100) String name) {
    return seedService.addLanguage(name);
  }

  @DeleteMapping("/seed/languages")
  @PreAuthorize("hasRole('ADMIN')")
  public String removeLanguage(@RequestParam @NotBlank String name) {
    return seedService.removeLanguage(name);
  }

  // Interests
  @GetMapping("/seed/interests")
  @PreAuthorize("hasRole('ADMIN')")
  public List<String> getAllInterests() {
    return seedService.getAllInterests();
  }

  @PostMapping("/seed/interests")
  @PreAuthorize("hasRole('ADMIN')")
  public String addInterest(@RequestParam @NotBlank @Size(max = 100) String name) {
    return seedService.addInterest(name);
  }

  @DeleteMapping("/seed/interests")
  @PreAuthorize("hasRole('ADMIN')")
  public String removeInterest(@RequestParam @NotBlank String name) {
    return seedService.removeInterest(name);
  }

  // Tags
  @GetMapping("/seed/tags")
  @PreAuthorize("hasRole('ADMIN')")
  public List<String> getAllTags() {
    return seedService.getAllTags();
  }

  @PostMapping("/seed/tags")
  @PreAuthorize("hasRole('ADMIN')")
  public String addTag(@RequestParam @NotBlank @Size(max = 100) String name) {
    return seedService.addTag(name);
  }

  @DeleteMapping("/seed/tags")
  @PreAuthorize("hasRole('ADMIN')")
  public String removeTag(@RequestParam @NotBlank String name) {
    return seedService.removeTag(name);
  }
}
