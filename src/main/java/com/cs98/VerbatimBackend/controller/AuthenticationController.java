package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.LoginRequest;
import com.cs98.VerbatimBackend.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cs98.VerbatimBackend.misc.Status;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;
    @PostMapping(path = "api/v1/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(Status.EMAIL_TAKEN).build();
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(Status.USERNAME_TAKEN).build();
        }
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .numGlobalChallengesCompleted(0)
                .numCustomChallengesCompleted(0)
                .streak(0)
                .hasCompletedDailyChallenge(false)
                .build();

        return ResponseEntity.ok(userRepository.save(newUser));

    }

    @PostMapping(path = "api/v1/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User userToAuthenticate;
        if (ObjectUtils.isEmpty(request.getEmailOrUsername())) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (userRepository.existsByEmail(request.getEmailOrUsername())) {
            userToAuthenticate = userRepository.findByEmail(request.getEmailOrUsername());
        }
        else if (userRepository.existsByUsername(request.getEmailOrUsername())) {
            userToAuthenticate = userRepository.findByUsername(request.getEmailOrUsername());
        }
        else {
            return ResponseEntity.status(Status.BAD_REQUEST).build();
        }
        
        if (!userToAuthenticate.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(Status.WRONG_PASSWORD).build();
        }

        return ResponseEntity.ok(userToAuthenticate);

    }
}
