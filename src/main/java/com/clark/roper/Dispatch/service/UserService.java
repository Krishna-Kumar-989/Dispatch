package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long getIdFromUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not Found"))
                .getId();
    }

    public String getUsernameFromId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUsername();
    }


}
