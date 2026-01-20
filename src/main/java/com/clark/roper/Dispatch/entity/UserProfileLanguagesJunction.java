package com.clark.roper.Dispatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userProfile_Languages_junction",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_profile_id", "language_id"})
})
public class UserProfileLanguagesJunction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_profile_id",nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "language_id",nullable = false)
    private Languages languages;





}
