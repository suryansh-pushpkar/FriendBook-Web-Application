package com.friendbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


public class CommentResponseDTO {
    private Long id;
    private String text;
    private String username;
    private String profileImage;
    private LocalDateTime createdAt;

    public CommentResponseDTO( ){
        super();
    }

    public CommentResponseDTO(Long id, String text, String username, String profileImage, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}