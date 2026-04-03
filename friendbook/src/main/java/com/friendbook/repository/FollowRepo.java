package com.friendbook.repository;

import java.util.List;
import java.util.Optional;

import com.friendbook.entity.Follow;
import com.friendbook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FollowRepo extends JpaRepository<Follow, Long> {


    long countByFollower(User follower);

    long countByFollowing(User following);

    List<Follow> findByFollowing(User following);

    List<Follow> findByFollower(User follower);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}