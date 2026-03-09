package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_blocks", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "blocker_id", "blocked_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class UserBlock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blocker_id", nullable = false)
  private User blocker;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blocked_id", nullable = false)
  private User blocked;

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
