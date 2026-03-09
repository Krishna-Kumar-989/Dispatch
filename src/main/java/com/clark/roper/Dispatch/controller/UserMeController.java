package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.UserBioRequest;
import com.clark.roper.Dispatch.dto.UserProfileCreateRequest;
import com.clark.roper.Dispatch.dto.UserProfileEditRequest;
import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.service.UserMeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("user/me")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeService userMeService;

    // View my profile
    @GetMapping("/profile")
    public UserProfileViewResponse viewMyProfile(@RequestHeader("Authorization") String authorizationHeader) {
        return userMeService.viewMyProfile(authorizationHeader);
    }

    // Upload profile picture
    @PostMapping("/profile-picture")
    public String uploadProfilePicture(@RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) {
        return userMeService.uploadProfilePicture(file, authorizationHeader);
    }

    // Create profile
    @PostMapping("/createProfile")
    public String createProfile(@Valid @RequestBody UserProfileCreateRequest userProfileCreateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        return userMeService.create(userProfileCreateRequest, authorizationHeader);
    }

    // Edit Profile
    @PatchMapping("/editProfile")
    public String editProfile(@Valid @RequestBody UserProfileEditRequest userProfileEditRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        return userMeService.edit(userProfileEditRequest, authorizationHeader);
    }

    // Create or Update Bio
    @PutMapping("/bio")
    public String updateBio(@Valid @RequestBody UserBioRequest userBioRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        return userMeService.updateBio(userBioRequest, authorizationHeader);
    }
}
