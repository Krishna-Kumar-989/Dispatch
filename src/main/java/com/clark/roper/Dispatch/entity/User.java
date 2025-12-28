package com.clark.roper.Dispatch.entity;

import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false,updatable = false)
    private Instant created;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRolesEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @PrePersist
    protected void OnCreate()
    {
        this.created = Instant.now();
        this.status  = UserStatus.FREE;
    }


}
