package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.AuthResponse;
import com.clark.roper.Dispatch.dto.LoginRequest;
import com.clark.roper.Dispatch.dto.RegisterRequest;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.repository.UserProfileRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest registerRequest) {

        // Check if username already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new BadRequestException("Username '" + registerRequest.getUsername() + "' is already taken");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRolesEnum.USER);
        user.setStatus(UserStatus.FREE);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("User registration failed for username '{}': {}", registerRequest.getUsername(), e.getMessage(),
                    e);
            throw new BadRequestException("User registration failed. Please try again.");
        }

        // Auto-login: generate token immediately
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, false);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException("Invalid username or password");
        }

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            if (user.getStatus() == UserStatus.BANNED) {
                throw new BadRequestException("Your account has been suspended");
            }

            String token = jwtService.generateToken(loginRequest.getUsername());
            boolean hasProfile = userProfileRepository.existsByUser(user);
            return new AuthResponse(token, hasProfile);
        }

        throw new BadRequestException("Authentication failed");
    }
}