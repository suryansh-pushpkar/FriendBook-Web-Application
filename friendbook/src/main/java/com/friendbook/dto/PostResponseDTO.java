package com.friendbook.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostResponseDTO {
    private Long id;
    private String caption;
    private String username;
    private String profileImage;
    private List<MediaDTO> mediaList;
    private List<CommentResponseDTO> comments;
    private int likeCount;
    private boolean isLikedByCurrentUser;


}