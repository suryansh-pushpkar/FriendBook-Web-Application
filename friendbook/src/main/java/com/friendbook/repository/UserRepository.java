package com.friendbook.repository;

import java.util.Optional;
import java.util.Set;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.friendbook.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndPassword(String username, String password);
    Set<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String username, String fullName);
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Set<User> searchByKeyword(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followerCount = u.followerCount + 1 WHERE u.id = :id")
    void incrementFollowers(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followingCount = u.followingCount + 1 WHERE u.id = :id")
    void incrementFollowing(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followerCount = u.followerCount - 1 WHERE u.id = :id")
    void decrementFollowers(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followingCount = u.followingCount - 1 WHERE u.id = :id")
    void decrementFollowing(@Param("id") Long id);
}