package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  Page<AuditLog> findByPerformedBy(String username, Pageable pageable);

  Page<AuditLog> findByAction(String action, Pageable pageable);
}
