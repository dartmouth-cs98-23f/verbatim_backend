package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.FriendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.badRequest().build();
        }

        UserRelationship newRel = UserRelationship.builder()
                .requestingFriend(requestingUser)
                .requestedFriend(requestedUser)
                .active(false)
                .build();

        userRelationshipRepository.save(newRel);

        return ResponseEntity.ok("success");


    }

}
