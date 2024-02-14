package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.RegisterRequest;
import com.cs98.VerbatimBackend.request.ResetPasswordRequest;
import com.cs98.VerbatimBackend.response.GetUserStatsResponse;
import com.cs98.VerbatimBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import com.cs98.VerbatimBackend.request.AccountSettingsRequest;
import com.cs98.VerbatimBackend.misc.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class AccountSettingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "api/v1/accountSettings")
    public ResponseEntity<User> accountSettings(@RequestBody AccountSettingsRequest request) {
        User user;

        // if user does not exist, return bad request
        if (!userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }
        // if the user exists, update changed fields
        else if (userRepository.existsByUsername(request.getUsername())) {
            user = userRepository.findByUsername(request.getUsername());

            if (request.getNewUsername() != null &&                            // make sure username is not null
                    !request.getNewUsername().isEmpty() &&                     // make sure username is not empty
                    !request.getNewUsername().equals(user.getUsername())) {    // don't update if not changed
                Optional<User> userOptional = Optional.ofNullable(userRepository.
                        findByUsername(request.getNewUsername()));
                if (userOptional.isPresent()) {                             // check if username is taken
                    return ResponseEntity.status(Status.USERNAME_TAKEN).build();
                }
                user.setUsername(request.getNewUsername());
            }

            if (request.getFirstName() != null &&                           // make sure first name is not null
                    !request.getFirstName().isEmpty() &&                    // make sure first name is not empty
                    !request.getFirstName().equals(user.getFirstName())) {  // don't update if not changed
                user.setFirstName(request.getFirstName());
            }

            if (request.getLastName() != null &&                            // make sure last name is not null
                    !request.getLastName().isEmpty() &&                     // make sure last name is not empty
                    !request.getLastName().equals(user.getLastName())) {    // don't update if not changed
                user.setLastName(request.getLastName());
            }

            if (request.getBio() != null &&                                 // make sure bio is not null (ok if empty)
                    !request.getBio().equals(user.getBio())) {              // don't update if not changed
                user.setBio(request.getBio());
            }

            if (request.getEmail() != null &&                               // make sure email is not null
                    !request.getEmail().isEmpty() &&                        // make sure email is not empty
                    !request.getEmail().equals(user.getEmail())) {          // don't update if not changed
                Optional<User> userOptional = Optional.ofNullable(userRepository.
                        findByEmail(request.getEmail()));
                if (userOptional.isPresent()) {                             // check if email is taken
                    return ResponseEntity.status(Status.EMAIL_TAKEN).build();
                }
                user.setEmail(request.getEmail());
            }

            if (request.getProfilePic() != null &&                           // make sure first name is not null
                    !request.getProfilePic().isEmpty() &&                    // make sure first name is not empty
                    !request.getProfilePic().equals(user.getProfilePicture())) {  // don't update if not changed
                user.setProfilePicture(request.getProfilePic());
            }

            return ResponseEntity.ok(userRepository.save(user));             // save the user
        } else {
            return ResponseEntity.status(Status.BAD_REQUEST).build();
        }
    }

    @PostMapping (path = "api/v1/resetPassword")
    public ResponseEntity<User> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user;

        // if user does not exist, return bad request
        if (!userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        // if the user exists, update password
        else if (userRepository.existsByUsername(request.getUsername())) {
            user = userRepository.findByUsername(request.getUsername());

            // make sure old password matches user's current password
            if (!request.getOldPassword().equals(user.getPassword())) {
                return ResponseEntity.status(Status.WRONG_PASSWORD).build();
            }

            // don't update if not changed
            if(request.getNewPassword().equals(request.getOldPassword())) {
                return ResponseEntity.status(Status.OLD_PASSWORD_SAME_AS_NEW_PASSWORD).build();
            }

            if (!request.getNewPassword().isEmpty()) {                      // make sure new password is not empty
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));                 // update the password
                return ResponseEntity.ok(userRepository.save(user));        // save the user
            } else {
                return ResponseEntity.status(Status.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(Status.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "api/v1/{yourusername}/{targetusername}/getUserStats")
    public ResponseEntity<GetUserStatsResponse> getUserStats(@PathVariable String yourusername,
                                                             @PathVariable String targetusername) {
        User yourUser;
        User targetUser;
        // if user does not exist, return bad request
        if (!userRepository.existsByUsername(targetusername) || !userRepository.existsByUsername(yourusername)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }
        // if the user exists, update changed fields
        else  {
            targetUser = userRepository.findByUsername(targetusername);
            yourUser = userRepository.findByUsername(yourusername);
        }

        GetUserStatsResponse response = userService.getUserStats(yourUser, targetUser);

        if (ObjectUtils.isEmpty(response)) {
            return ResponseEntity.status(Status.FETCH_USER_STATS_FAILED).build();
        }

        return ResponseEntity.ok(response);

    }
}
