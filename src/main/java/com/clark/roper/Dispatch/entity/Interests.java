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
@Table(name = "interests")
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interest",nullable = false)
    private String interest;

    @PrePersist
    protected void OnCreate(){
        if(interest == null)
        {
            this.interest = "SLEEPING";
        }
    }
}
