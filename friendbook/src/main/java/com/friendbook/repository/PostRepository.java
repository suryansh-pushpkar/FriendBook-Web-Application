package com.friendbook.repository;

import com.friendbook.entity.Post;
import com.friendbook.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"user"})
    Slice<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Slice<Post> findByUserInOrderByCreatedAtDesc(Collection<User> users, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Slice<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "mediaList", "likes"})
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}