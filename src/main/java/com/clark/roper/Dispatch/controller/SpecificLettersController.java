package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.SpecificLettersRequest;
import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.service.SpecificLettersService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/specific_letter")
@AllArgsConstructor
public class SpecificLettersController {

    private final SpecificLettersService specificLettersService;


    @PostMapping("/send")
    public String sendToSpecificLetters(@RequestBody SpecificLettersRequest specificLettersRequest,
                                        @RequestHeader("Authorization") String authorizationHeader)
    {
          return specificLettersService.send(specificLettersRequest,authorizationHeader);

    }
}
