package com.friendbook.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserService(UserRepository userRepo, PostRepository postRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }
    @Transactional
    public UserDTO registerUser(User user) {
        user.setUsername(UsernameUtil.generateUniqueUsername(user.getFullName(), userRepo));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User dbUser = userRepo.save(user);
        return modelMapper.map(dbUser, UserDTO.class);
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

    @Transactional
    public Optional<User> updateProfile(String username, ProfileUpdateDTO dto) {
        return userRepo.findByUsername(username).map(user -> {
            user.setFullName(normalize(dto.getFullName()));
            user.setEmail(normalize(dto.getEmail()));
            user.setProfileImage(normalize(dto.getProfileImage()));
            user.setFavSongs(normalize(dto.getFavSongs()));
            user.setFavBooks(normalize(dto.getFavBooks()));
            user.setFavPlaces(normalize(dto.getFavPlaces()));

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
            }

            return userRepo.save(user);
        });
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}