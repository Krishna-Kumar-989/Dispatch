package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.SpecificLettersRequest;
import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.repository.SpecificLettersRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecificLettersService {

    private final JwtService jwtService;
    private final SpecificLettersRepository specificLettersRepository;
    private final UserRepository userRepository;


    @Transactional
   public String send(SpecificLettersRequest specificLettersRequest,String authHeader)
    {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }


        String jwt;
        jwt = authHeader.substring(7);

        String username;
        username = jwtService.extractUsername(jwt);

        SpecificLetters specificLetters = new SpecificLetters();

        User sender = userRepository.findByUsername(username).orElseThrow();

        specificLetters.setSender(sender);

        Long ReceiverId = specificLettersRequest.getReceiverId();

        User receiver = userRepository.findById(ReceiverId).orElseThrow();

        specificLetters.setReceiver(receiver);

        String content  = specificLettersRequest.getContent();

        specificLetters.setContent(content);


        specificLettersRepository.save(specificLetters);







        return"Success";
    }





}
