package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.FriendAcceptOrDeclineRequest;
import com.cs98.VerbatimBackend.request.FriendRequest;
import com.cs98.VerbatimBackend.response.InboundFriendRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class UserRelationshipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;


    @PostMapping("api/v1/addFriend")
    public ResponseEntity<String> addFriend(@RequestBody FriendRequest request) {
        User requestingUser = userRepository.findByUsername(request.getRequestingUsername());
        User requestedUser = userRepository.findByUsername(request.getRequestedUsername());

        if (ObjectUtils.isEmpty(requestingUser) || ObjectUtils.isEmpty(requestedUser)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        UserRelationship newRel = UserRelationship.builder()
                .requestingFriend(requestingUser)
                .requestedFriend(requestedUser)
                .active(false)
                .build();

        userRelationshipRepository.save(newRel);

        return ResponseEntity.ok("success");

    }

    @PostMapping(path = "api/v1/getFriendRequests")
    public ResponseEntity<List<User>> getFriendRequests(@RequestBody String username) {
        User clientUser = userRepository.findByUsername(username);

        List<UserRelationship> userRelationshipsWithClientRequested = userRelationshipRepository.findByRequestedFriendIdAndActiveFalse(clientUser.getId());

        List<User> friendRequests = new ArrayList<>();

        for (UserRelationship rel : userRelationshipsWithClientRequested) {
            friendRequests.add(rel.getRequestingFriend());
        }
        return ResponseEntity.ok(friendRequests);
    }

    @PostMapping(path = "api/v1/handleFriendRequest")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody FriendAcceptOrDeclineRequest request) {
        User requestingUser = userRepository.findByUsername(request.getRequestingUsername());
        User requestedUser = userRepository.findByUsername(request.getRequestedUsername());

        if (ObjectUtils.isEmpty(requestingUser) || ObjectUtils.isEmpty(requestedUser)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (!userRelationshipRepository.existsByRequestingFriendIdAndRequestedFriendId(
                requestingUser.getId(), requestedUser.getId())) {
            return ResponseEntity.status(Status.BAD_REQUEST).build();
        }

        UserRelationship relationship = userRelationshipRepository.
                findByRequestingFriendIdAndRequestedFriendId(requestingUser.getId(), requestedUser.getId());

        if (request.getAccept()) {
            relationship.setActive(true);
            userRelationshipRepository.save(relationship);
        }
        else {
            userRelationshipRepository.delete(relationship);
        }
        return ResponseEntity.ok("success");
    }

    @PostMapping(path = "api/v1/getFriends")
    public ResponseEntity<List<User>> getFriends(@RequestBody String username) {
        User clientUser = userRepository.findByUsername(username);
        List<UserRelationship> activeFriends = userRelationshipRepository.findActiveFriendsByUserId(clientUser.getId());

        List<User> friendsList = new ArrayList<>();
        for (UserRelationship relationship : activeFriends) {
            if (relationship.getRequestingFriend().getId().equals(clientUser.getId())) {
                friendsList.add(relationship.getRequestedFriend());
            }
            else {
                friendsList.add(relationship.getRequestingFriend());
            }
        }
        return ResponseEntity.ok(friendsList);

    }

}
