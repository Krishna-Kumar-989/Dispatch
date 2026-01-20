package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.SpecificLettersReceivedViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersSendRequest;
import com.clark.roper.Dispatch.dto.SpecificLettersViewResponse;
import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.service.SpecificLettersService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specific_letter")
@AllArgsConstructor
public class SpecificLettersController {

    private final SpecificLettersService specificLettersService;


    @PostMapping("/send")
    public String sendToSpecificLetters(@RequestBody SpecificLettersSendRequest specificLettersSendRequest,
                                        @RequestHeader("Authorization") String authorizationHeader)
    {
          return specificLettersService.send(specificLettersSendRequest,authorizationHeader);

    }

    /** feature under development
    @PostMapping("/view/received")
    public List<SpecificLettersViewResponse> receivedLetters
            (@RequestBody SpecificLettersReceivedViewRequestFilter filterRequest,@RequestHeader("Authorization") String authHeader)
    {
        return specificLettersService.viewReceived(filterRequest,authHeader);
    }
    **/
}
