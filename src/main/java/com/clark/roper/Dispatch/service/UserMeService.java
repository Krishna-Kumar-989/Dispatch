package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.UserBioRequest;
import com.clark.roper.Dispatch.dto.UserProfileCreateRequest;
import com.clark.roper.Dispatch.dto.UserProfileEditRequest;
import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.*;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserMeService {

    private final JwtService jwtService;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileLanguagesJunctionRepository userProfileLanguagesJunctionRepository;
    private final LanguagesRepository languagesRepository;
    private final InterestsRepository interestsRepository;
    private final UserProfileInterestsJunctionRepository userProfileInterestsJunctionRepository;
    private final UserBioRepository userBioRepository;
    private final ProfileMapper profileMapper;
    private final ImageUploadService imageUploadService;

    // View My Profile:

    @Transactional
    public UserProfileViewResponse viewMyProfile(String authHeader) {
        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElse(null);

        if (userProfile == null) {
            return null; // No profile created yet
        }

        return profileMapper.toViewResponse(userProfile, user);
    }

    // Upload Profile Picture :

    @Transactional
    public String uploadProfilePicture(MultipartFile file, String authHeader) {
        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found — create one first"));

        String url = imageUploadService.upload(file);
        userProfile.setProfilePictureUrl(url);
        userProfileRepository.save(userProfile);

        return url;
    }

    //Create Profile :

    @Transactional
    public String create(UserProfileCreateRequest userProfileCreateRequest, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userProfileRepository.existsByUser(user)) {
            throw new BadRequestException("User profile already exists");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);

        // Set profile fields
        userProfile.setGender(userProfileCreateRequest.getGender());
        userProfile.setDateOfBirth(userProfileCreateRequest.getDateOfBirth());
        userProfile.setCountry(userProfileCreateRequest.getCountry());

        userProfileRepository.save(userProfile);

        // Set Languages junction
        Set<String> languages = userProfileCreateRequest.getLanguages();
        for (String languageName : languages) {
            Languages languageElement = languagesRepository.findByLanguage(languageName)
                    .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + languageName));

            UserProfileLanguagesJunction junction = new UserProfileLanguagesJunction();
            junction.setUserProfile(userProfile);
            junction.setLanguages(languageElement);
            userProfileLanguagesJunctionRepository.save(junction);
        }

        // Set Interests junction
        Set<String> interests = userProfileCreateRequest.getInterests();
        for (String interestName : interests) {
            Interests interestElement = interestsRepository.findByInterest(interestName)
                    .orElseThrow(() -> new ResourceNotFoundException("Interest not found: " + interestName));

            UserProfileInterestsJunction junction = new UserProfileInterestsJunction();
            junction.setUserProfile(userProfile);
            junction.setInterests(interestElement);
            userProfileInterestsJunctionRepository.save(junction);
        }

        return "Success";
    }

    // Edit Profile:

    @Transactional
    public String edit(UserProfileEditRequest req, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found — create one first"));

        // Update non-junction fields (only if provided)
        if (req.getGender() != null) {
            userProfile.setGender(req.getGender());
        }
        if (req.getDateOfBirth() != null) {
            userProfile.setDateOfBirth(req.getDateOfBirth());
        }
        if (req.getCountry() != null) {
            userProfile.setCountry(req.getCountry());
        }

        // Update languages (delete old, insert new)
        if (req.getLanguages() != null) {
            userProfileLanguagesJunctionRepository.deleteByUserProfile(userProfile);
            userProfileLanguagesJunctionRepository.flush();

            for (String langName : req.getLanguages()) {
                Languages language = languagesRepository.findByLanguage(langName)
                        .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + langName));

                UserProfileLanguagesJunction junction = new UserProfileLanguagesJunction();
                junction.setUserProfile(userProfile);
                junction.setLanguages(language);
                userProfileLanguagesJunctionRepository.save(junction);
            }
        }

        // Update interests (delete old, insert new)
        if (req.getInterests() != null) {
            userProfileInterestsJunctionRepository.deleteByUserProfile(userProfile);
            userProfileInterestsJunctionRepository.flush();

            for (String interestName : req.getInterests()) {
                Interests interest = interestsRepository.findByInterest(interestName)
                        .orElseThrow(() -> new ResourceNotFoundException("Interest not found: " + interestName));

                UserProfileInterestsJunction junction = new UserProfileInterestsJunction();
                junction.setUserProfile(userProfile);
                junction.setInterests(interest);
                userProfileInterestsJunctionRepository.save(junction);
            }
        }

        return "Success";
    }

    // Update Bio

    @Transactional
    public String updateBio(UserBioRequest userBioRequest, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserBio userBio = userBioRepository.findByUser(user)
                .orElseGet(() -> {
                    UserBio newBio = new UserBio();
                    newBio.setUser(user);
                    return newBio;
                });

        userBio.setBio(userBioRequest.getBio());
        userBioRepository.save(userBio);

        return "Success";
    }
}
