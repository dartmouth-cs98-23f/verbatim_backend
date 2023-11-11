package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.UserDetailsPrincipal;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.RoleRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.LoginRequest;
import com.cs98.VerbatimBackend.request.RegisterRequest;
import com.cs98.VerbatimBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cs98.VerbatimBackend.misc.Status;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

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
                .password(passwordEncoder.encode(request.getPassword()))
                .numGlobalChallengesCompleted(0)
                .numCustomChallengesCompleted(0)
                .role(roleRepository.findByRolename("ROLE_USER"))
                .streak(0)
                .hasCompletedDailyChallenge(false)
                .build();

        return ResponseEntity.ok(userRepository.save(newUser));

    }

    @PostMapping(path = "api/v1/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {

        UserDetailsPrincipal userToAuthenticate = userService.loadUserByUsername(request.getEmailOrUsername());

        if (passwordEncoder.matches(request.getPassword(), userToAuthenticate.getPassword())) {
            return ResponseEntity.ok(userToAuthenticate.getUser());
        }
        return ResponseEntity.status(Status.WRONG_PASSWORD).build();

    }
}
