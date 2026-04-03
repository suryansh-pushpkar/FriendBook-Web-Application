package com.friendbook.controller;

import com.friendbook.entity.Comment;
import com.friendbook.entity.Post;
import com.friendbook.entity.User;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestParam("caption") String caption,
                                        @RequestParam("files") List<MultipartFile> files,
                                        Principal principal) throws Exception {
        User currentUser = userService.findByEmail(principal.getName());
        Post savedPost = postService.createPost(currentUser, caption, files);

        return ResponseEntity.ok(Map.of("status", "success",
                "username", currentUser.getUsernameField()));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Map<String, Object> response = postService.toggleLike(user, postId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @RequestBody Map<String, String> payload,
                                        Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Comment savedComment = postService.addComment(user, postId, payload.get("text"));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment added successfully");
        response.put("username", user.getUsernameField());
        response.put("text", savedComment.getText());
        return ResponseEntity.ok(response);
    }
}