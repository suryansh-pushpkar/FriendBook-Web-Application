package com.friendbook.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.friendbook.entity.User;
import com.friendbook.service.UserService;

@Controller
public class PageController {

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/profile/{username}")
    public String showProfilePage(@PathVariable String username, Model model) {
        return userService.findByUsername(username)
                .map(user -> {
                    populateProfileModel(model, user);
                    return "profile";
                })
                .orElse("redirect:/login?error=notfound");
    }

    private void populateProfileModel(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("posts", userService.findPostsByUser(user));
        model.addAttribute("postCount", user.getPosts() != null ? user.getPosts().size() : 0);
        model.addAttribute("followerCount", user.getFollowers() != null ? user.getFollowers().size() : 0);
        model.addAttribute("followingCount", user.getFollowing() != null ? user.getFollowing().size() : 0);
    }
    @GetMapping("/settings/profile")
    public String showSettingsPage() {
        return "edit-profile"; // The name of your new HTML file
    }
}