package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cs98.VerbatimBackend.request.AccountSettingsRequest;

@RestController
public class AccountSettingsController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "api/v1/accountSettings")
    public ResponseEntity<User> register(@RequestBody AccountSettingsRequest request) {
        User user;

        // if user does not exist, return bad request
        if (ObjectUtils.isEmpty(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        // if the user exists, update all fields
        else if (userRepository.existsByUsername(request.getUsername())) {
            user = userRepository.findByUsername(request.getUsername());
            user.setUsername(request.getUsername());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setBio(request.getBio());
            user.setEmail(request.getEmail());
            user.setProfilePicture(request.getProfilePic());

            // save the user
            return ResponseEntity.ok(userRepository.save(user));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
