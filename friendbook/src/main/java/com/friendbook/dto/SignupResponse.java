package com.friendbook.dto;

public class SignupResponse {

    public boolean success;
    public String message;

    public SignupResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}