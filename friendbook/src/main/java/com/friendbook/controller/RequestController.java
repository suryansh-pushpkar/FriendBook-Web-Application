package com.friendbook.controller;

import com.friendbook.entity.User;
import com.friendbook.service.FriendRequestService;
import com.friendbook.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class RequestController {
    private final FriendRequestService requestService;
    private final UserService userService;

    public RequestController(FriendRequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping("/follow/{id}")
    public Map<String, String> follow(@PathVariable Long id, Principal principal) {
        User sender = userService.findByEmail(principal.getName());
        String status = requestService.toggleRequest(sender, id);
        return Map.of("status", status);
    }

    @PostMapping("/request/respond")
    public void respond(@RequestParam Long requestId, @RequestParam boolean accept) {
        requestService.respondToRequest(requestId, accept);
    }
}