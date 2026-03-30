package com.friendbook.repository;
import java.util.List;

import com.friendbook.entity.Media;
import com.friendbook.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {

    @EntityGraph(attributePaths = {"post"})
    List<Media> findByPost(Post post);
}