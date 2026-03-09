package com.clark.roper.Dispatch.entity;

import com.clark.roper.Dispatch.enums.ReportStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private User reporter;

  @Column(nullable = false)
  private String targetType; // GENERAL_LETTER, SPECIFIC_LETTER, REPLY, USER

  @Column(nullable = false)
  private Long targetId;

  @Column(nullable = false)
  private String reason; // SPAM, HARASSMENT, INAPPROPRIATE, OTHER

  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportStatusEnum status = ReportStatusEnum.PENDING;

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  private Instant reviewedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
