package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.UserProfileViewResponse;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.*;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOtherService {

        private final JwtService jwtService;
        private final UserProfileRepository userProfileRepository;
        private final UserRepository userRepository;
        private final ProfileMapper profileMapper;

        @Transactional
        public UserProfileViewResponse view(Long id, String authHeader) {

                // Validate the auth header
                JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);

                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));

                UserProfile userProfile = userProfileRepository.findByUser(user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profile not found for user ID " + id));

                return profileMapper.toViewResponse(userProfile, user);
        }
}
