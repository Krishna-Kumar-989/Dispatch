package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_connections")
@Getter
@Setter
public class UserConnections {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1",nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2",nullable = false)
    private User user2;

    @Column(name = "lastSentU1")
    private Instant lastSentUser1;

    @Column(name = "lastReadU1")
    private Instant lastReadUser1;

    @Column(name = "lastSentU2")
    private Instant lastSentUser2;

    @Column(name = "lastReadU2")
    private Instant lastReadUser2;

    @Column(name = "lastInteracted")
    private Instant lastInteracted;


}
