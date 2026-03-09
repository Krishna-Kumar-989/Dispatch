package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.service.BlockService;
import com.clark.roper.Dispatch.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


 //Social features : blocking and following.

@RestController
@RequestMapping("/api/v1/social")
@AllArgsConstructor
public class SocialController {

  private final BlockService blockService;
  private final FollowService followService;


  //Blocking :

  @PostMapping("/block/{userId}")
  public String blockUser(@PathVariable("userId") Long userId,
      @RequestHeader("Authorization") String authHeader) {
    return blockService.blockUser(userId, authHeader);
  }

  @DeleteMapping("/block/{userId}")
  public String unblockUser(@PathVariable("userId") Long userId,
      @RequestHeader("Authorization") String authHeader) {
    return blockService.unblockUser(userId, authHeader);
  }

  @GetMapping("/blocked")
  public List<Long> getBlockedUsers(@RequestHeader("Authorization") String authHeader) {
    return blockService.getBlockedUserIds(authHeader);
  }

  //Following :

  @PostMapping("/follow/{userId}")
  public String follow(@PathVariable("userId") Long userId,
      @RequestHeader("Authorization") String authHeader) {
    return followService.follow(userId, authHeader);
  }

  @DeleteMapping("/follow/{userId}")
  public String unfollow(@PathVariable("userId") Long userId,
      @RequestHeader("Authorization") String authHeader) {
    return followService.unfollow(userId, authHeader);
  }

  @GetMapping("/followers/{userId}")
  public Page<Map<String, Object>> getFollowers(@PathVariable("userId") Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return followService.getFollowers(userId, page, size);
  }

  @GetMapping("/following/{userId}")
  public Page<Map<String, Object>> getFollowing(@PathVariable("userId") Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return followService.getFollowing(userId, page, size);
  }

  @GetMapping("/follow-counts/{userId}")
  public Map<String, Long> getFollowCounts(@PathVariable("userId") Long userId) {
    return followService.getCounts(userId);
  }
}
