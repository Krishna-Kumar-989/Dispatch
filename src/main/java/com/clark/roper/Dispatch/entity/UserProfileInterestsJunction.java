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
@Table(name = "userProfile_Interests_Junction",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_profile_id","interest_id"})
        }
)
public class UserProfileInterestsJunction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String Id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_profile_id",nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "interest_id",nullable = false)
    private Interests  interests;


}
