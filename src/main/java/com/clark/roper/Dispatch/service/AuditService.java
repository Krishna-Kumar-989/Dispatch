package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.AuditLog;
import com.clark.roper.Dispatch.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuditService {

  private static final int MAX_PAGE_SIZE = 100;

  private final AuditLogRepository auditLogRepository;


   //Log an action .

  public void log(String action, String performedBy, String targetType, Long targetId, String details) {
    AuditLog entry = new AuditLog();
    entry.setAction(action);
    entry.setPerformedBy(performedBy);
    entry.setTargetType(targetType);
    entry.setTargetId(targetId);
    entry.setDetails(details);
    auditLogRepository.save(entry);
  }


   //Admin: get paginated audit logs

  public Page<Map<String, Object>> getLogs(int page, int size) {
    return auditLogRepository
        .findAll(PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").descending()))
        .map(this::toMap);
  }

  public Page<Map<String, Object>> getLogsByUser(String username, int page, int size) {
    return auditLogRepository
        .findByPerformedBy(username,
            PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").descending()))
        .map(this::toMap);
  }

  private Map<String, Object> toMap(AuditLog log) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", log.getId());
    map.put("action", log.getAction());
    map.put("performedBy", log.getPerformedBy());
    map.put("targetType", log.getTargetType());
    map.put("targetId", log.getTargetId());
    map.put("details", log.getDetails());
    map.put("createdAt", log.getCreatedAt());
    return map;
  }
}
