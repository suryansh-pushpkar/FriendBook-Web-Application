package com.friendbook.controller;

import com.friendbook.entity.Post;
import com.friendbook.service.FriendRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.friendbook.entity.User;
import com.friendbook.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class PageController {

    private final UserService userService;
    private FriendRequestService requestService;

    public PageController(UserService userService, FriendRequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
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

    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "query", required = false) String query, HttpServletRequest request, Model model) {

        if (query != null && !query.trim().isEmpty()) {
            Set<User> searchResults = userService.searchUsers(query);
            model.addAttribute("users", searchResults);
            model.addAttribute("query", query);
        }
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "fragments/search-results :: resultsList";
        }
        return "search";
    }
    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);
        model.addAttribute("pendingRequests", requestService.getPendingRequests(currentUser));
        model.addAttribute("currentUsername", currentUser.getUsernameField());
        return "notifications";
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

    @GetMapping("/post/upload")
    public String showCreatePostPage() {
        return "create-post";
    }

    @GetMapping("/profile/{identifier}")
    public String showProfile(@PathVariable String identifier, Model model, Principal principal) {
        User targetUser = userService.findByIdentifier(identifier);
        String loggedInEmail = principal.getName();
        User visitor = userService.findByEmail(loggedInEmail);

        boolean isOwnProfile = visitor.getId().equals(targetUser.getId());
        String status = requestService.getRelationshipStatus(visitor, targetUser);
        model.addAttribute("requestStatus", status);
        model.addAttribute("user", targetUser);
        model.addAttribute("isOwnProfile", isOwnProfile);
        model.addAttribute("posts", targetUser.getPosts());
        model.addAttribute("postCount", targetUser.getPosts().size());
        model.addAttribute("followerCount", targetUser.getFollowers().size());
        model.addAttribute("followingCount", targetUser.getFollowing().size());
        return "profile";
    }

    @GetMapping("/explore")
    public String explorePage() {
        return "explore";
    }
}