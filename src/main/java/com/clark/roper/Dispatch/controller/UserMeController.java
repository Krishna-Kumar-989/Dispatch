package com.clark.roper.Dispatch.controller;


import com.clark.roper.Dispatch.dto.UserProfileCreateRequest;

import com.clark.roper.Dispatch.dto.UserProfileEditRequest;
import com.clark.roper.Dispatch.service.UserMeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/me")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeService userMeService;


    //Create profile
    @PostMapping("/createProfile")
    public String CreateProfile(@RequestBody UserProfileCreateRequest userProfileCreateRequest, @RequestHeader("Authorization") String authorizationHeader){


         return  userMeService.create(userProfileCreateRequest,authorizationHeader);

    }

    //Edit Profile
    @PatchMapping("/editProfile")
    public String EditProfile(@RequestBody UserProfileEditRequest userProfileEditRequest, @RequestHeader("Authorization") String authorizationHeader){
        return userMeService.edit(userProfileEditRequest,authorizationHeader);
    }



}
