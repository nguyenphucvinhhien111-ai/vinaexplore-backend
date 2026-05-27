package com.vinatour.backend.controller;

import com.vinatour.backend.Service.UserFollowerService;
import com.vinatour.backend.dto.response.UserResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/followers")
public class UserFollowerController {

    private final UserFollowerService userFollowerService;

    
    @PostMapping("/{followerId}/follow/{followedId}")
    public ResponseEntity<String> toggleFollow(
            @PathVariable Integer followerId, 
            @PathVariable Integer followedId) {
        return ResponseEntity.ok(userFollowerService.toggleFollow(followerId, followedId));
    }

    @GetMapping("/{userId}/following")
    public List<UserResponseDTO> getFollowings(@PathVariable Integer userId) {
        return userFollowerService.getfollowing(userId);
    }

    @GetMapping("/{userId}/followers")
    public List<UserResponseDTO> getFollowers(@PathVariable Integer userId) {
        return userFollowerService.getfollowers(userId);
    }
}