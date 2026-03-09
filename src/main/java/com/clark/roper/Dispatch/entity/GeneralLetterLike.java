package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "general_letter_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "letter_id", "user_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class GeneralLetterLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "letter_id", nullable = false)
  private GeneralLetter letter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
