package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.configuration.PasswordConfig;
import com.clark.roper.Dispatch.dto.LoginRequest;
import com.clark.roper.Dispatch.dto.RegisterRequest;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest)
    {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){

        return authService.login(loginRequest);

    }






}
