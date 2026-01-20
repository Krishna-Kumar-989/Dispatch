package com.clark.roper.Dispatch.controller;


import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.service.UserOtherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/other")
@AllArgsConstructor
public class UserOtherController{

     private final UserOtherService userOtherService;

     @GetMapping("/view/{id}")
     public UserProfileViewResponse ViewProfile(@PathVariable("id") Long id, @RequestHeader("Authorization") String authHeader)
     {
            return   userOtherService.view(id,authHeader);
     }


}
