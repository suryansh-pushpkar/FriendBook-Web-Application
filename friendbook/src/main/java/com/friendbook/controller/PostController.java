package com.friendbook.controller;

import com.friendbook.dto.CommentRequestDTO;
import com.friendbook.dto.CommentResponseDTO;
import com.friendbook.dto.MediaDTO;
import com.friendbook.dto.PostResponseDTO;
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
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long postId, @RequestBody CommentRequestDTO requestDTO, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Comment comment = postService.addComment(user, postId, requestDTO.getText());
        CommentResponseDTO response = new CommentResponseDTO(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsernameField(),
                comment.getUser().getProfileImage(),
                comment.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostDetails(@PathVariable Long postId, Principal principal) {
        Post post = postService.findById(postId);
        User currentUser = userService.findByEmail(principal.getName());
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setCaption(post.getCaption());
        dto.setUsername(post.getUser().getUsernameField());
        dto.setProfileImage(post.getUser().getProfileImage());
        List<MediaDTO> mediaDtos = post.getMediaList().stream().map(m -> {
            MediaDTO mDto = new MediaDTO();
            mDto.setFilePath(m.getFilePath());
            mDto.setMediaType(m.getMediaType());
            return mDto;
        }).toList();
        dto.setMediaList(mediaDtos);
        List<CommentResponseDTO> commentDtos = post.getComments().stream().map(c -> {
            CommentResponseDTO cDto = new CommentResponseDTO();
            cDto.setId(c.getId());
            cDto.setText(c.getText());
            cDto.setUsername(c.getUser().getUsernameField());
            cDto.setProfileImage(c.getUser().getProfileImage());
            cDto.setCreatedAt(c.getCreatedAt());
            return cDto;
        }).toList();
        dto.setComments(commentDtos);
        dto.setLikeCount(post.getLikes().size());
        boolean liked = post.getLikes().stream()
                .anyMatch(l -> l.getUser().getId().equals(currentUser.getId()));
        dto.setLikedByCurrentUser(liked);

        return ResponseEntity.ok(dto);
    }
}