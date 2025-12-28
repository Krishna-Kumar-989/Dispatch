package com.clark.roper.Dispatch.entity;

import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "specific_letters")
public class SpecificLetters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id",nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id",nullable = false)
    private User receiver;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private SpecificLettersStatusEnum status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void OnCreate()
    {
        this.status = SpecificLettersStatusEnum.SENT;
        this.createdAt = Instant.now();

    }









}

