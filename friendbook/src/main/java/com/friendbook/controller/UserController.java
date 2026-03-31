package com.friendbook.controller;

import com.friendbook.dto.*;
import com.friendbook.entity.User;
import com.friendbook.service.UserService;
import com.friendbook.utility.CaptchaUtil;
import com.friendbook.utility.JwtUtil;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService detailsService;
    private final JwtUtil jwt;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          UserDetailsService detailsService,
                          JwtUtil jwt) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.detailsService = detailsService;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    @RateLimiter(name = "authLimiter", fallbackMethod = "loginRateLimitFallback")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginDTO dto, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        UserDetails userDetails = detailsService.loadUserByUsername(dto.getEmail());
        String token = jwt.generateToken(userDetails);

        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600*60);
        response.addCookie(jwtCookie);

        User user = userService.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDTO userProfile = new UserResponseDTO();
        userProfile.setId(user.getId());
        userProfile.setFullName(user.getFullName());
        userProfile.setEmail(user.getEmail());
        userProfile.setUsername(user.getUsernameField());
        userProfile.setProfileImage(user.getProfileImage());
        userProfile.setFavSongs(user.getFavSongs());
        userProfile.setFavBooks(user.getFavBooks());
        userProfile.setFavPlaces(user.getFavPlaces());


        userProfile.setFollowerCount(user.getFollowers() != null ? user.getFollowers().size() : 0);
        userProfile.setFollowingCount(user.getFollowing() != null ? user.getFollowing().size() : 0);
        userProfile.setPostCount(user.getPosts() != null ? user.getPosts().size() : 0);

        Map<String, Object> hm = new HashMap<>();
        hm.put("message", "Sign in success");
        hm.put("token", token);
        hm.put("userProfile", userProfile);
        hm.put("redirectUrl", "/profile/" + user.getUsernameField());

        return ResponseEntity.ok(hm);
    }

    @PostMapping("/signup")
    @RateLimiter(name = "authLimiter", fallbackMethod = "signupRateLimitFallback")
    public ResponseEntity<SignupResponse> signup(@RequestBody UserDTO dto) {
        boolean captchaVerified = CaptchaUtil.verifyCaptcha(dto.getCaptchaToken());
        if (!captchaVerified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SignupResponse(false, "Security Check Failed: Bot activity detected."));
        }
        try {
            User user = new User();
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());

            UserDTO registeredUser = userService.registerUser(user);
            if (registeredUser != null) {
                return ResponseEntity.ok(new SignupResponse(true, "Registration Successful!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new SignupResponse(false, "Registration failed."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SignupResponse(false, "An account with this email already exists."));
        }
    }

    @PutMapping(value = "/profile/{username}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(@PathVariable String username,
                                           @ModelAttribute ProfileUpdateDTO dto,
                                           @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            return userService.updateProfile(username, dto, file)
                    .map(updatedUser -> {
                        UserResponseDTO response = new UserResponseDTO();
                        response.setId(updatedUser.getId());
                        response.setFullName(updatedUser.getFullName());
                        response.setUsername(updatedUser.getUsernameField());
                        response.setProfileImage(updatedUser.getProfileImage());
                        response.setFavSongs(updatedUser.getFavSongs());
                        response.setFavBooks(updatedUser.getFavBooks());
                        response.setFavPlaces(updatedUser.getFavPlaces());

                        response.setFollowerCount(updatedUser.getFollowers().size());
                        response.setFollowingCount(updatedUser.getFollowing().size());
                        response.setPostCount(updatedUser.getPosts().size());

                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<Map<String, String>> loginRateLimitFallback(UserLoginDTO dto, HttpServletResponse response, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message", "Too many login attempts. Please try again after 2 minutes."));
    }

    public ResponseEntity<SignupResponse> signupRateLimitFallback(UserDTO dto, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new SignupResponse(false, "Too many signup attempts. Please try again later."));
    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtToken", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);

        return "redirect:/login?logout";
    }
}