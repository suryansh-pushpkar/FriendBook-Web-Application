package com.friendbook.repository;

import com.friendbook.entity.FriendRequest;
import com.friendbook.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsBySenderAndReceiver(User sender, User receiver);

    @EntityGraph(attributePaths = {"sender"})
    List<FriendRequest> findByReceiverAndAcceptedFalse(User receiver);

    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

    List<FriendRequest> findByReceiverAndStatus(User receiver, String status);

    Optional<FriendRequest> findBySenderAndReceiverAndStatus(User sender, User receiver, String status);
}