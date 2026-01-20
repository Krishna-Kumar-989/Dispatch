package com.clark.roper.Dispatch.service;


import com.clark.roper.Dispatch.dto.UserProfileCreateRequest;
import com.clark.roper.Dispatch.dto.UserProfileEditRequest;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.enums.UserGenderEnum;
import com.clark.roper.Dispatch.repository.*;
import com.clark.roper.Dispatch.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserMeService {

    private final JwtService jwtService;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileLanguagesJunctionRepository userProfileLanguagesJunctionRepository;
    private final LanguagesRepository languagesRepository;
    private final InterestsRepository interestsRepository;
    private final UserProfileInterestsJunctionRepository userProfileInterestsJunctionRepository;



    //Create profile
    @Transactional
    public String create(UserProfileCreateRequest userProfileCreateRequest, String authHeader)
    {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }


        String jwt;
        jwt = authHeader.substring(7);

        String username;
        username = jwtService.extractUsername(jwt);

        User user =  userRepository.findByUsername(username).orElseThrow();

        if (userProfileRepository.existsByUser(user)) {
            throw new RuntimeException("User profile already exists");
        }



        UserProfile userProfile = new UserProfile();

        userProfile.setUser(user);

        //Extract data
        UserGenderEnum gender = userProfileCreateRequest.getGender();
        LocalDate dateOfBirth = userProfileCreateRequest.getDateOfBirth();
        String country = userProfileCreateRequest.getCountry();

        Set<String> languages = userProfileCreateRequest.getLanguages();

        Set<String> interests = userProfileCreateRequest.getInterests();



        //Set data
        userProfile.setGender(gender);
        userProfile.setDateOfBirth(dateOfBirth);
        userProfile.setCountry(country);

        userProfileRepository.save(userProfile);

        //Set junction tables

            //Languages Junction table


               for(String languageName : languages)
                {
                    UserProfileLanguagesJunction userProfileLanguagesJunction = new UserProfileLanguagesJunction();

                    Languages languageElement = new Languages();
                    languageElement = languagesRepository.findByLanguage(languageName).orElseThrow();



                    userProfileLanguagesJunction.setUserProfile(userProfile);
                    userProfileLanguagesJunction.setLanguages(languageElement);

                    userProfileLanguagesJunctionRepository.save(userProfileLanguagesJunction);
                }

           // Interests Junction table
                for (String interestName : interests)
                   {
                      UserProfileInterestsJunction userProfileInterestsJunction = new UserProfileInterestsJunction();

                      Interests interestElement = interestsRepository.findByInterest(interestName).orElseThrow();

                      userProfileInterestsJunction.setUserProfile(userProfile);
                      userProfileInterestsJunction.setInterests(interestElement);

                      userProfileInterestsJunctionRepository.save(userProfileInterestsJunction);
                    }








        return"Success";
    }





    //Edit
    @Transactional
    public String edit(UserProfileEditRequest req, String authHeader)
    {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        //extract user
        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        //set non junction fields

        if (req.getGender() != null) {
            userProfile.setGender(req.getGender());
        }

        if (req.getDateOfBirth() != null) {
            userProfile.setDateOfBirth(req.getDateOfBirth());
        }

        if (req.getCountry() != null) {
            userProfile.setCountry(req.getCountry());
        }

        /* will implement later
        //languages

        if (req.getLanguages() != null) {

            //delete old
            userProfileLanguagesJunctionRepository
                    .deleteByUserProfile(userProfile);

            for (String langName : req.getLanguages()) {

                Languages language = languagesRepository
                        .findByName(langName)
                        .orElseThrow(() ->
                                new RuntimeException("Language not found: " + langName));

                UserProfileLanguagesJunction junction =
                        new UserProfileLanguagesJunction(userProfile, language);

                userProfileLanguagesJunctionRepository.save(junction);
            }
        }

        //interests

        if (req.getInterests() != null) {

            userProfileInterestsJunctionRepository
                    .deleteByUserProfile(userProfile);

            for (String interestName : req.getInterests()) {

                Interests interest = interestsRepository
                        .findByName(interestName)
                        .orElseThrow(() ->
                                new RuntimeException("Interest not found: " + interestName));

                UserProfileInterestsJunction junction =
                        new UserProfileInterestsJunction(userProfile, interest);

                userProfileInterestsJunctionRepository.save(junction);
            }
        }
        **/




        return "Success";
    }




}
