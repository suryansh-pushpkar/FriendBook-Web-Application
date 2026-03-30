
package com.friendbook.repository;

import com.friendbook.entity.Comment;
import com.friendbook.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    Slice<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

}
