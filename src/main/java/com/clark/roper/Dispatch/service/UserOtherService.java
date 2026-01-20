package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.enums.UserGenderEnum;
import com.clark.roper.Dispatch.repository.*;
import com.clark.roper.Dispatch.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserOtherService {

    private JwtService jwtService;
    private UserProfileRepository userProfileRepository;
    private UserRepository userRepository;
    private UserProfileLanguagesJunctionRepository userProfileLanguagesJunctionRepository;
    private UserProfileInterestsJunctionRepository userProfileInterestsJunctionRepository;


    @Transactional
    public UserProfileViewResponse view(Long id, String authHeader)
    {
        /*
         **will be implemented later for blocking users to see profiles**

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }


        String jwt;
        jwt = authHeader.substring(7);

        String viewerUsername;
        viewerUsername = jwtService.extractUsername(jwt);
         */



        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not exists"));
        UserProfile userProfile = userProfileRepository.findByUser(user).orElseThrow();

        //object to store response
        UserProfileViewResponse userProfileViewResponse = new UserProfileViewResponse();

        //get fields without junction table

        UserGenderEnum gender = userProfile.getGender();
        LocalDate dateOfBirth = userProfile.getDateOfBirth();
        String country = userProfile.getCountry();

        //get fields with junction table

          //Languages
        Set<String> languagesSet = new HashSet<>();

        List<UserProfileLanguagesJunction > userProfileLanguagesJunction = userProfileLanguagesJunctionRepository.findByUserProfile(userProfile);

        for(UserProfileLanguagesJunction i : userProfileLanguagesJunction)
        {

            Languages lang = i.getLanguages();
            String langString = lang.getLanguage();
            languagesSet.add(langString);
        }

         //Interests
        Set<String> interestsSet = new HashSet<>();

        List<UserProfileInterestsJunction> userProfileInterestsJunctions = userProfileInterestsJunctionRepository.findByUserProfile(userProfile);

        for(UserProfileInterestsJunction i : userProfileInterestsJunctions)
        {

            Interests interest = i.getInterests();
            String interestString = interest.getInterest();
            interestsSet.add(interestString);
        }


        //set fields

        userProfileViewResponse.setCountry(country);
        userProfileViewResponse.setGender(gender);
        userProfileViewResponse.setDateOfBirth(dateOfBirth);
        userProfileViewResponse.setLanguages(languagesSet);
        userProfileViewResponse.setInterests(interestsSet);





      return userProfileViewResponse;






    }
}
