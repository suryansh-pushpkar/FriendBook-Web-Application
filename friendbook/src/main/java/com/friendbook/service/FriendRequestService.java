package com.friendbook.service;

import com.friendbook.entity.Follow;
import com.friendbook.entity.FriendRequest;
import com.friendbook.entity.User;
import com.friendbook.repository.FollowRepo;
import com.friendbook.repository.FriendRequestRepository;
import com.friendbook.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    private final FriendRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final FollowRepo followRepo ;
    public FriendRequestService(FriendRequestRepository requestRepo, UserRepository userRepo, FollowRepo followRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.followRepo = followRepo;
    }

    @Transactional
    public String toggleRequest(User sender, Long receiverId) {
        User receiver = userRepo.findById(receiverId).orElseThrow();
        Optional<FriendRequest> existing = requestRepo.findBySenderAndReceiver(sender, receiver);
        if (existing.isPresent()) {
            FriendRequest fr = existing.get();
            if (fr.isAccepted()) {
                followRepo.deleteByFollowerAndFollowing(sender, receiver);
                followRepo.deleteByFollowerAndFollowing(receiver, sender);
                sender.getFollowing().remove(receiver);
                receiver.getFollowers().remove(sender);
                receiver.getFollowing().remove(sender);
                sender.getFollowers().remove(receiver);
                userRepo.save(sender);
                userRepo.save(receiver);
                userRepo.decrementFollowers(receiver.getId());
                userRepo.decrementFollowing(sender.getId());
                userRepo.decrementFollowers(sender.getId());
                userRepo.decrementFollowing(receiver.getId());
            }
            requestRepo.delete(fr);
            return "NONE";
        }
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");
        request.setAccepted(false);
        requestRepo.save(request);
        return "PENDING";
    }
    @Transactional
    public void respondToRequest(Long requestId, boolean accept) {
        FriendRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (accept) {
            request.setAccepted(true);
            request.setStatus("ACCEPTED");
            requestRepo.save(request);
            User sender = request.getSender();
            User receiver = request.getReceiver();
            Follow follow1 = new Follow();
            follow1.setFollower(sender);
            follow1.setFollowing(receiver);
            followRepo.save(follow1);

            Follow follow2 = new Follow();
            follow2.setFollower(receiver);
            follow2.setFollowing(sender);
            followRepo.save(follow2);

            // Update the Set collections for consistency
            sender.getFollowing().add(receiver);
            receiver.getFollowers().add(sender);
            userRepo.save(sender);
            userRepo.save(receiver);
            userRepo.incrementFollowing(sender.getId());
            userRepo.incrementFollowers(receiver.getId());
            userRepo.incrementFollowing(receiver.getId());
            userRepo.incrementFollowers(sender.getId());

        } else {
            requestRepo.delete(request);
        }
    }
    public String getRelationshipStatus(User sender, User receiver) {
        Optional<FriendRequest> rel = requestRepo.findBySenderAndReceiver(sender, receiver);
        if (rel.isEmpty()) {
            return "NONE";
        }
        FriendRequest request = rel.get();
        if (request.isAccepted()) {
            return "ACCEPTED";
        } else {
            return "PENDING";
        }
    }
    public List<FriendRequest> getPendingRequests(User user) {
        return requestRepo.findByReceiverAndStatus(user, "PENDING");
    }
}