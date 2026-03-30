package com.friendbook.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;


public class ProfileUpdateDTO {
    private String fullName;
    private String email;
    private String password;
    private String favSongs;
    private String favBooks;
    private String favPlaces;
    private MultipartFile file;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFavSongs() {
        return favSongs;
    }

    public void setFavSongs(String favSongs) {
        this.favSongs = favSongs;
    }

    public String getFavBooks() {
        return favBooks;
    }

    public void setFavBooks(String favBooks) {
        this.favBooks = favBooks;
    }

    public String getFavPlaces() {
        return favPlaces;
    }

    public void setFavPlaces(String favPlaces) {
        this.favPlaces = favPlaces;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}