package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.SpecificLettersReceivedViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersSendRequest;
import com.clark.roper.Dispatch.dto.SpecificLettersSentViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersViewResponse;
import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import com.clark.roper.Dispatch.service.SpecificLettersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/specific-letter")
@AllArgsConstructor
public class SpecificLettersController {

    private final SpecificLettersService specificLettersService;



    @PostMapping("/send")
    public String sendToSpecificLetters(@Valid @RequestBody SpecificLettersSendRequest specificLettersSendRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        return specificLettersService.send(specificLettersSendRequest, authorizationHeader);
    }

    //Returns Page
    @GetMapping("/view/received")
    public Page<SpecificLettersViewResponse> viewReceivedLetters(
            @RequestParam(name = "sender-id", required = false) Long senderId,
            @RequestParam(name = "status", required = false) SpecificLettersStatusEnum status,
            @RequestParam(name = "from", required = false) Instant createdAtLowerLimit,
            @RequestParam(name = "to", required = false) Instant createdAtHigherLimit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        SpecificLettersReceivedViewRequestFilter filterRequest = new SpecificLettersReceivedViewRequestFilter();
        filterRequest.setStatus(status);
        filterRequest.setSenderId(senderId);
        filterRequest.setCreatedAtHigherLimit(createdAtHigherLimit);
        filterRequest.setCreatedAtLowerLimit(createdAtLowerLimit);

        return specificLettersService.viewReceived(filterRequest, authHeader, page, size);
    }

    @GetMapping("/view/sent")
    public Page<SpecificLettersViewResponse> viewSentLetters(
            @RequestParam(name = "receiver-id", required = false) Long receiverId,
            @RequestParam(name = "status", required = false) SpecificLettersStatusEnum status,
            @RequestParam(name = "from", required = false) Instant createdAtLowerLimit,
            @RequestParam(name = "to", required = false) Instant createdAtHigherLimit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        SpecificLettersSentViewRequestFilter filterRequest = new SpecificLettersSentViewRequestFilter();
        filterRequest.setStatus(status);
        filterRequest.setReceiverId(receiverId);
        filterRequest.setCreatedAtHigherLimit(createdAtHigherLimit);
        filterRequest.setCreatedAtLowerLimit(createdAtLowerLimit);

        return specificLettersService.viewSent(filterRequest, authHeader, page, size);
    }

    @PatchMapping("/read")
    public String setLetterStatusRead(@RequestParam(name = "letter-id") Long letterId,
            @RequestHeader("Authorization") String authHeader) {
        return specificLettersService.setStatusRead(letterId, authHeader);
    }

    // Soft delete
    @DeleteMapping("/{id}")
    public String deleteSpecificLetter(@PathVariable("id") Long letterId,
            @RequestHeader("Authorization") String authHeader) {
        return specificLettersService.softDelete(letterId, authHeader);
    }
}
