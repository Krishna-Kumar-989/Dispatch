package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long getIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username))
                .getId();
    }

    public String getUsernameFromId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id))
                .getUsername();
    }
}
