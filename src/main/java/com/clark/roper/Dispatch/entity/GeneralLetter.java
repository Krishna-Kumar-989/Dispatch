package com.clark.roper.Dispatch.entity;

import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "general_letters")
@Getter
@Setter
@NoArgsConstructor
public class GeneralLetter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SpecificLettersStatusEnum status;

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  private Instant updatedAt;

  @Column(nullable = false)
  private int replyCount = 0;

  @Column(nullable = false)
  private int likeCount = 0;

  private Instant scheduledAt; // null = immediate, set = publish at this time

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    if (this.status == null) {
      this.status = SpecificLettersStatusEnum.SENT;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now();
  }
}
