package com.clark.roper.Dispatch.service;


import com.clark.roper.Dispatch.dto.SpecificLettersReceivedViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersSendRequest;
import com.clark.roper.Dispatch.dto.SpecificLettersViewResponse;
import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.repository.SpecificLettersRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SpecificLettersService {

    private final JwtService jwtService;
    private final SpecificLettersRepository specificLettersRepository;
    private final UserRepository userRepository;


   //send letters
    @Transactional
   public String send(SpecificLettersSendRequest specificLettersRequest, String authHeader)
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




    //view received letters
    public List<SpecificLettersViewResponse> viewReceived
    (SpecificLettersReceivedViewRequestFilter filterRequest,String authHeader)
    {
        //get receiver id aka login user
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }


        String jwt;
        jwt = authHeader.substring(7);

        String username;
        username = jwtService.extractUsername(jwt);


        User receiver = userRepository.findByUsername(username).orElseThrow();


        //get view request filtered data

         List<SpecificLetters > specificLettersList;

        specificLettersList = specificLettersRepository.filterLetters(
                filterRequest.getSenderId(),
                filterRequest.getStatus(),
                filterRequest.getCreatedAtLowerLimit(),
                filterRequest.getCreatedAtHigherLimit()
        );


        //set the dto from filtered letters
          //create object
          List<SpecificLettersViewResponse> dtoResponseList = new ArrayList<>();

        for(SpecificLetters letter : specificLettersList)
        {
            SpecificLettersViewResponse dtoResponse = new SpecificLettersViewResponse();

            dtoResponse.setId(letter.getId());
            dtoResponse.setSenderId(letter.getSender().getId());
            dtoResponse.setReceiverId(letter.getReceiver().getId());
            dtoResponse.setStatus(letter.getStatus());
            dtoResponse.setContent(letter.getContent());
            dtoResponse.setCreatedAt(letter.getCreatedAt());



            //add to list
            dtoResponseList.add(dtoResponse);
        }


         return dtoResponseList;
    }





}
