package com.friendbook.controller;

import com.friendbook.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class FollowController {
    private UserService userService;
    public FollowController(UserService userService) {
        this.userService = userService;
    }
@PostMapping("/follow/{id}")
    public ResponseEntity<?> followUser(@PathVariable Long id, Principal principal){
        boolean status = userService.toggleFollow(id,principal.getName());
        return ResponseEntity.ok(Map.of("following",status));

    }
}
