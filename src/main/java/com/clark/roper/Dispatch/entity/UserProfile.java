package com.clark.roper.Dispatch.entity;

import com.clark.roper.Dispatch.enums.UserGenderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User Relation
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;



    //Gender
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGenderEnum gender;

    //DOB
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    //Country
    @Column(name = "country",nullable = false)
    private String country;


   //language
    //implemented by junction table



   //Interests
     //implemented by junction table




    @PrePersist
    protected void onCreate() {

        if (this.gender == null) {
            this.gender = UserGenderEnum.MALE;
        }

        if (this.country == null) {
            this.country = "INDIA";
        }

        if (this.dateOfBirth == null) {
            this.dateOfBirth = LocalDate.of(2000, 1, 1);
        }
    }












}
