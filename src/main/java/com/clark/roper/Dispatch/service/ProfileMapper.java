package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.repository.UserBioRepository;
import com.clark.roper.Dispatch.repository.UserProfileInterestsJunctionRepository;
import com.clark.roper.Dispatch.repository.UserProfileLanguagesJunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//mapper UserProfile + User  <-> DTO


@Component
@RequiredArgsConstructor
public class ProfileMapper {

  private final UserProfileLanguagesJunctionRepository userProfileLanguagesJunctionRepository;
  private final UserProfileInterestsJunctionRepository userProfileInterestsJunctionRepository;
  private final UserBioRepository userBioRepository;

  public UserProfileViewResponse toViewResponse(UserProfile userProfile, User user) {
    UserProfileViewResponse response = new UserProfileViewResponse();
    response.setGender(userProfile.getGender());
    response.setDateOfBirth(userProfile.getDateOfBirth());
    response.setCountry(userProfile.getCountry());
    response.setProfilePictureUrl(userProfile.getProfilePictureUrl());

    // Languages
    Set<String> languagesSet = new HashSet<>();
    List<UserProfileLanguagesJunction> langJunctions = userProfileLanguagesJunctionRepository
        .findByUserProfile(userProfile);
    for (UserProfileLanguagesJunction j : langJunctions) {
      languagesSet.add(j.getLanguages().getLanguage());
    }
    response.setLanguages(languagesSet);

    // Interests
    Set<String> interestsSet = new HashSet<>();
    List<UserProfileInterestsJunction> intJunctions = userProfileInterestsJunctionRepository
        .findByUserProfile(userProfile);
    for (UserProfileInterestsJunction j : intJunctions) {
      interestsSet.add(j.getInterests().getInterest());
    }
    response.setInterests(interestsSet);

    // Bio
    userBioRepository.findByUser(user)
        .ifPresent(bio -> response.setBio(bio.getBio()));

    return response;
  }
}
