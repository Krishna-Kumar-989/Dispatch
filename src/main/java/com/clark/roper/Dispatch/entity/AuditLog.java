package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String action; // USER_REGISTERED, LETTER_SENT, STATUS_CHANGED, USER_BANNED, etc.

  @Column(nullable = false)
  private String performedBy; // username of who performed the action

  private String targetType; // USER, LETTER, PROFILE, etc.

  private Long targetId;

  @Column(columnDefinition = "TEXT")
  private String details; // JSON or description of what changed

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
