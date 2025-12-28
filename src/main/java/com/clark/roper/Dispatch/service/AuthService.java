package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.LoginRequest;
import com.clark.roper.Dispatch.dto.RegisterRequest;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public String register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRolesEnum.USER);
        user.setStatus(UserStatus.FREE);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("User registration failed", e);
        }

        return "User Registered Successfully";
    }


    public String login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
           authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        } catch (Exception e) {
            throw new RuntimeException("User Login failed", e);
        }

        String response = "";
        if(authentication.isAuthenticated())
        {
            response =  jwtService.generateToken(loginRequest.getUsername());
        }

        return response;

    }

}