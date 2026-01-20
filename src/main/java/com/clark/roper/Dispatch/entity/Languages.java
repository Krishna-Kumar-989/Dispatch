package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "languages")
public class Languages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "language",updatable = true)
    private String language;

    @PrePersist
    protected void OnCreate()
    {
        if (this.language == null) {
            this.language = "ENGLISH";
        }
    }
}
