package com.friendbook.dto;

public class ProfileUpdateDTO {

    private String fullName;
    private String email;
    private String password;
    private String profileImage;
    private String favSongs;
    private String favBooks;
    private String favPlaces;

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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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
}