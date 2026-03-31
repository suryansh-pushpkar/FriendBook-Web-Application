package com.friendbook.controller;

import com.friendbook.entity.Post;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.friendbook.entity.User;
import com.friendbook.service.UserService;

import java.util.List;

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


    private void populateProfileModel(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("posts", userService.findPostsByUser(user));
        model.addAttribute("postCount", user.getPosts() != null ? user.getPosts().size() : 0);
        model.addAttribute("followerCount", user.getFollowers() != null ? user.getFollowers().size() : 0);
        model.addAttribute("followingCount", user.getFollowing() != null ? user.getFollowing().size() : 0);
    }
    @GetMapping("/settings/profile")
    public String showSettingsPage() {
        return "edit-profile";
    }
    @GetMapping("/profile/{identifier}")
    public String showProfilePage(@PathVariable String identifier, Model model) {
        User user = userService.findByUsername(identifier)
                .orElseGet(() -> userService.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));

        List<Post> userPosts = userService.findPostsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", userPosts);
        model.addAttribute("postCount", userPosts.size());
        model.addAttribute("followerCount", user.getFollowers().size());
        model.addAttribute("followingCount", user.getFollowing().size());

        return "profile";
    }

    @GetMapping("/explore")
    public String explorePage() {
        return "explore";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications";
    }
}