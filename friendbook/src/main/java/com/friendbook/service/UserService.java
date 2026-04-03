package com.friendbook.service;

import java.nio.file.*;
import java.util.*;

import com.friendbook.Exception.UserNotFoundException;
import com.friendbook.entity.FriendRequest;
import com.friendbook.repository.FriendRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.dto.ProfileUpdateDTO;
import com.friendbook.dto.UserDTO;
import com.friendbook.entity.Post;
import com.friendbook.entity.User;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.utility.UsernameUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final FriendRequestRepository friendReqRepo;

    private final String UPLOAD_DIR = "C:/Users/Admin/Desktop/Friendbook/assets/";

    public UserService(UserRepository userRepo, PostRepository postRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, FriendRequestRepository FriendRequestRepository, FriendRequestRepository friendReqRepo ) {
        this.userRepo = userRepo;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.friendReqRepo = friendReqRepo;
    }

    public Set<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return userRepo.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(query, query);
    }

    @Transactional
    public UserDTO registerUser(User user) {
        user.setUsername(UsernameUtil.generateUniqueUsername(user.getFullName(), userRepo));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User dbUser = userRepo.save(user);
        return modelMapper.map(dbUser, UserDTO.class);
    }

    public User findByIdentifier(String identifier) {
        return userRepo.findByUsername(identifier)
                .orElseGet(() -> userRepo.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found: " + identifier)));
    }

@Transactional
    public Map<String, Object> toggleFollow(Long targetUserId, String currentUserName){
        User currentUser = userRepo.findByEmail(currentUserName).orElseThrow(() -> new RuntimeException(" user not found: " + currentUserName));
        User targetUser = userRepo.findById(targetUserId).orElseThrow(() -> new RuntimeException("Target user not found: " + targetUserId));
        if(currentUser.getId().equals(targetUser.getId())){
            throw new RuntimeException("Cannot follow yourself");
        }
    FriendRequest request = new FriendRequest();
    if(currentUser.getFollowing().contains(targetUser)){
            currentUser.getFollowing().remove(targetUser);
            targetUser.getFollowers().remove(currentUser);
        } else {
            request.setSender(currentUser);
            request.setReceiver(targetUser);
            request.setStatus("pending");
            request.setAccepted(false);
        friendReqRepo.save(request);
        }
    Map<String, Object> response = new HashMap<>();
    response.put("status", "" + (currentUser.getFollowing().contains(targetUser) ? "following" : "not_following"));
    response.put("followerCount", targetUser.getFollowers().size());
    return response;
    }


    @Transactional
    public Optional<User> updateProfile(String username, ProfileUpdateDTO dto, MultipartFile file) {
        return userRepo.findByUsername(username).map(user -> {
            if (dto.getFullName() != null) user.setFullName(normalize(dto.getFullName()));
            if (dto.getFavSongs() != null) user.setFavSongs(normalize(dto.getFavSongs()));
            if (dto.getFavBooks() != null) user.setFavBooks(normalize(dto.getFavBooks()));
            if (dto.getFavPlaces() != null) user.setFavPlaces(normalize(dto.getFavPlaces()));
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
            }

            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = saveFile(file);
                    user.setProfileImage(fileName);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to store profile media: " + e.getMessage());
                }
            }

            return userRepo.save(user);
        });
    }


    private String saveFile(MultipartFile file) throws Exception {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public User authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<Post> findPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }
}