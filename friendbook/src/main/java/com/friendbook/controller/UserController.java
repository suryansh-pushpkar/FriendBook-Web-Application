package com.friendbook.controller;

import com.friendbook.dto.SignupResponse;
import com.friendbook.dto.ProfileUpdateDTO;
import com.friendbook.dto.UserDTO;
import com.friendbook.dto.UserLoginDTO;
import com.friendbook.utility.CaptchaUtil;
import com.friendbook.utility.JwtUtil;

import java.util.Map;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.friendbook.entity.User;
import com.friendbook.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    @RateLimiter(name = "authLimiter", fallbackMethod = "signupRateLimitFallback")
    public ResponseEntity<SignupResponse> signup(@RequestBody UserDTO dto) {
        boolean captchaVerified = CaptchaUtil.verifyCaptcha(dto.getCaptchaToken());
        if (!captchaVerified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SignupResponse(false, "Security Check Failed: Bot activity detected."));
        }
        try {

            User user = modelMapper.map(dto, User.class);
            UserDTO registeredUser = userService.registerUser(user);
            if (registeredUser != null) {
                return ResponseEntity.ok(new SignupResponse(true, "Registration Successful! Welcome to FriendBook."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new SignupResponse(false, "Registration failed. Please check your details."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SignupResponse(false, "An account with this email already exists."));
        }
    }

    @PostMapping("/login")
    @RateLimiter(name = "authLimiter", fallbackMethod = "loginRateLimitFallback")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginDTO dto) {
        User user = userService.authenticateUser(dto.getEmail(), dto.getPassword());
        if (user != null) {

            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok().body(Map.of(
                    "token", token,
                    "message", "Login successful",
                    "username", user.getUsername(),
                    "redirectUrl", "/profile/" + user.getUsername()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Either email or password is incorrect try again"));
        }
    }

    public ResponseEntity<SignupResponse> signupRateLimitFallback(UserDTO dto, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new SignupResponse(false, "Too many attempts. Please try again after 2 minutes."));
    }

    public ResponseEntity<Map<String, String>> loginRateLimitFallback(UserLoginDTO dto, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message", "Too many attempts. Please try again after 2 minutes."));
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateProfile(@PathVariable String username, @RequestBody ProfileUpdateDTO dto) {
        try {
            return userService.updateProfile(username, dto)
                    .<ResponseEntity<?>>map(updatedUser -> ResponseEntity.ok(Map.of(
                            "message", "Profile updated successfully",
                            "username", updatedUser.getUsername(),
                            "fullName", updatedUser.getFullName() == null ? "" : updatedUser.getFullName(),
                            "email", updatedUser.getEmail() == null ? "" : updatedUser.getEmail(),
                            "profileImage", updatedUser.getProfileImage() == null ? "" : updatedUser.getProfileImage(),
                            "favSongs", updatedUser.getFavSongs() == null ? "" : updatedUser.getFavSongs(),
                            "favBooks", updatedUser.getFavBooks() == null ? "" : updatedUser.getFavBooks(),
                            "favPlaces", updatedUser.getFavPlaces() == null ? "" : updatedUser.getFavPlaces()
                    )))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "User not found")));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Unable to update profile. Check whether the email is already in use."));
        }
    }
}