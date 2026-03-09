package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "general_letter_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "letter_id", "tag_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class LetterTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "letter_id", nullable = false)
  private GeneralLetter letter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;
}
