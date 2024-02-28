package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.UserGroupJunctionRepository;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.FriendAcceptOrDeclineRequest;
import com.cs98.VerbatimBackend.request.FriendRequest;
import com.cs98.VerbatimBackend.request.RemoveFriendRequest;
import com.cs98.VerbatimBackend.request.UserGroupCreationRequest;
import com.cs98.VerbatimBackend.response.*;
import com.cs98.VerbatimBackend.response.UserGroupCreationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class UserRelationshipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private UserGroupController userGroupController;

    @Autowired
    private UserGroupJunctionRepository userGroupJunctionRepository;


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

        // if user does not exist, return bad request
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        User clientUser = userRepository.findByUsername(username);

        List<UserRelationship> userRelationshipsWithClientRequested = userRelationshipRepository.findByRequestedFriendIdAndActiveFalse(clientUser.getId());

        List<User> friendRequests = new ArrayList<>();

        for (UserRelationship rel : userRelationshipsWithClientRequested) {
            friendRequests.add(rel.getRequestingFriend());
        }
        return ResponseEntity.ok(friendRequests);
    }

    @PostMapping(path = "api/v1/getUsersIHaveRequested")
    public ResponseEntity<List<User>> getUsersIHaveRequested(@RequestBody String username) {

        // if user does not exist, return bad request
        if(!userRepository.existsByUsername(username)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        // get the requesting user
        User clientUser = userRepository.findByUsername(username);

        // get a list of all UserRelationships where clientUser is the requesting user
        List<UserRelationship> userRelationshipsWithClientRequesting = userRelationshipRepository.findByRequestingFriendIdAndActiveFalse(clientUser.getId());

        // create an empty friends list
        List<User> friendRequests = new ArrayList<>();

        // loop through the UserRelationships and add each requested user to friendRequests
        for (UserRelationship rel : userRelationshipsWithClientRequesting) {
            friendRequests.add(rel.getRequestedFriend());
        }

        // return the requested users
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
            relationship.setDate(Date.valueOf(LocalDate.now()));
            userRelationshipRepository.save(relationship);

            // create a "group" with these two users

            // build the request
            UserGroupCreationRequest userGroupCreationRequest = new UserGroupCreationRequest();
            userGroupCreationRequest.setCreatedByUsername(requestingUser.getUsername());
            userGroupCreationRequest.setGroupName(requestingUser.getUsername() + "_" + requestedUser.getUsername());
            List<String> usernames = new ArrayList<>();
            usernames.add(requestedUser.getUsername());
            userGroupCreationRequest.setUsernamesToAdd(usernames);

            // create the group
            ResponseEntity<UserGroupCreationResponse> response = userGroupController.createGroup(userGroupCreationRequest);

            if (ObjectUtils.isEmpty(response)) { // make sure the response is not empty
                throw new RuntimeException("could not build response");
            }
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

    @GetMapping(path = "api/v1/{user}/{friend}/challenges")
    public ResponseEntity<GetActiveChallengesResponse> getActiveChallenges(@PathVariable String user,
                                                                           @PathVariable String friend) {
        // get user IDs
        int userId = userRepository.findByUsername(user).getId();
        int friendId = userRepository.findByUsername(friend).getId();

        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(friendId) || userId == friendId) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        // make sure the two users are friends
        if (!userRelationshipRepository.friendshipExists(userId, friendId)) {
            return ResponseEntity.status(Status.ACTIVE_FRIENDSHIP_NOT_FOUND).build();
        }

        // get the group ID for the friendship
        int groupId = userGroupJunctionRepository.getGroupIdForFriendGroup(userId, friendId);

        // get the active group challenges
        return userGroupController.getActiveChallenges(groupId);
    }

    @GetMapping(path = "api/v1/{user}/{friend}")
    public ResponseEntity<GroupStats> getGroupStats(@PathVariable String user,
                                                    @PathVariable String friend) {
        // get user IDs
        int userId = userRepository.findByUsername(user).getId();
        int friendId = userRepository.findByUsername(friend).getId();

        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(friendId) || userId == friendId) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        // make sure the two users are friends
        if (!userRelationshipRepository.friendshipExists(userId, friendId)) {
            return ResponseEntity.status(Status.ACTIVE_FRIENDSHIP_NOT_FOUND).build();
        }

        // get the group ID for the friendship
        int groupId = userGroupJunctionRepository.getGroupIdForFriendGroup(userId, friendId);

        // get stats
        return userGroupController.getGroupStats(groupId);

    }

    @PostMapping(path = "api/v1/removeFriend")
    public ResponseEntity<String> removeFriend(@RequestBody RemoveFriendRequest request) {
        User baseUser = userRepository.findByUsername(request.getBaseUsername());
        User userToRemove = userRepository.findByUsername(request.getUsernameToRemove());

        UserRelationship friendshipToRemove = userRelationshipRepository.findFriendship(baseUser.getId(), userToRemove.getId());

        if (ObjectUtils.isEmpty(friendshipToRemove)) {
            return ResponseEntity.status(Status.FRIENDSHIP_NOT_FOUND).build();
        }
        userRelationshipRepository.delete(friendshipToRemove);

        return ResponseEntity.ok("success");
    }

}
