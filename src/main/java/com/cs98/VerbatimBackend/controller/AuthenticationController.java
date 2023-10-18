package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;
    @PostMapping(path = "api/v1/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        return ResponseEntity.ok(userRepository.save(newUser));

    }
}
