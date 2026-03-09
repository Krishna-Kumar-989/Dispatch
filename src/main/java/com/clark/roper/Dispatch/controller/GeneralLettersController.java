package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.dto.*;
import com.clark.roper.Dispatch.service.GeneralLettersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/general-letter")
@AllArgsConstructor
public class GeneralLettersController {

  private final GeneralLettersService generalLettersService;

  @PostMapping("/create")
  public GeneralLetterViewResponse create(
      @Valid @RequestBody GeneralLetterCreateRequest request,
      @RequestHeader("Authorization") String authHeader) {
    return generalLettersService.create(request, authHeader);
  }

  @GetMapping("/list")
  public Page<GeneralLetterViewResponse> listAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {
    return generalLettersService.listAll(page, size, sortBy, direction);
  }

  // Search & Filter
  @GetMapping("/search")
  public Page<GeneralLetterViewResponse> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String tag,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return generalLettersService.search(keyword, tag, page, size);
  }

  @GetMapping("/my-letters")
  public Page<GeneralLetterViewResponse> listMine(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {
    return generalLettersService.listMine(authHeader, page, size, sortBy, direction);
  }

  @GetMapping("/{id}")
  public GeneralLetterViewResponse viewById(@PathVariable("id") Long letterId,
      @RequestHeader(value = "Authorization", required = false) String authHeader) {
    return generalLettersService.viewById(letterId, authHeader);
  }

  // Send draft
  @PatchMapping("/{id}/send")
  public String sendDraft(@PathVariable("id") Long letterId,
      @RequestHeader("Authorization") String authHeader) {
    return generalLettersService.sendDraft(letterId, authHeader);
  }

  @PostMapping("/{id}/reply")
  public GeneralLetterReplyResponse reply(
      @PathVariable("id") Long letterId,
      @Valid @RequestBody GeneralLetterReplyRequest request,
      @RequestHeader("Authorization") String authHeader) {
    return generalLettersService.reply(letterId, request, authHeader);
  }

  @GetMapping("/{id}/replies")
  public Page<GeneralLetterReplyResponse> viewReplies(
      @PathVariable("id") Long letterId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return generalLettersService.viewReplies(letterId, page, size);
  }

  // Like/Unlike toggle
  @PostMapping("/{id}/like")
  public String toggleLike(@PathVariable("id") Long letterId,
      @RequestHeader("Authorization") String authHeader) {
    return generalLettersService.toggleLike(letterId, authHeader);
  }

  // Soft delete
  @DeleteMapping("/{id}")
  public String delete(@PathVariable("id") Long letterId,
      @RequestHeader("Authorization") String authHeader) {
    return generalLettersService.delete(letterId, authHeader);
  }
}
