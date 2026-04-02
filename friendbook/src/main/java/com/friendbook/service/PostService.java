package com.friendbook.service;

import com.friendbook.entity.Comment;
import com.friendbook.entity.Media;
import com.friendbook.entity.Post;
import com.friendbook.entity.PostLike;
import com.friendbook.entity.User;
import com.friendbook.repository.CommentRepo;
import com.friendbook.repository.LikeRepository;
import com.friendbook.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final LikeRepository likeRepository;
    private final CommentRepo commentRepository;
    private final String UPLOAD_DIR = "../assets/";
    public PostService(PostRepository postRepo, LikeRepository likeRepository, CommentRepo commentRepository) {
        this.postRepo = postRepo;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }


    public Post createPost(User user, String caption, List<MultipartFile> files) throws Exception  {
        System.out.println("Creating post for user: " + user.getEmail() + ", caption: " + caption + ", files count: " + files.size());
        Post post = new Post();
        post.setCaption(caption);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        for(MultipartFile file : files) {
            if(file.isEmpty()) {
                System.out.println("Skipping empty file");
                continue;
            }
            System.out.println("Processing file: " + file.getOriginalFilename() + ", size: " + file.getSize());
            String fileName = saveFile(file);
            Media media = new Media();
            media.setFilePath(fileName);
            media.setMediaType(file.getContentType());
            media.setPost(post);
            post.getMediaList().add(media);
        }
        Post saved = postRepo.save(post);
        System.out.println("Post saved with id: " + saved.getId());
        return saved;
    }
    public Map<String, Object> toggleLike(User user, Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> existingLike = likeRepository.findByUserAndPost(user, post);
        Map<String, Object> response = new HashMap<>();
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            response.put("liked", false);
            response.put("count", post.getLikes().size() - 1);
        } else {
            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            response.put("liked", true);
            response.put("count", post.getLikes().size() + 1);
        }
        return response;
    }

    public Comment addComment(User user, Long postId, String text) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    private String saveFile(MultipartFile file) throws Exception {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}