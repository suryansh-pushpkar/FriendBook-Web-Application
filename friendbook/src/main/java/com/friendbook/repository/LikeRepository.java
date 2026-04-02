package com.friendbook.repository;

import com.friendbook.entity.Post;
import com.friendbook.entity.PostLike;
import com.friendbook.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<PostLike,Long> {

    boolean existsByUserAndPost(User user, Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLike l WHERE l.user = :user AND l.post = :post")
    void deleteByUserAndPost(User user, Post post);

    long countByPost(Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);
}