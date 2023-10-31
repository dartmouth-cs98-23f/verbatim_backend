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

import java.util.Optional;

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
        // if the user exists, update changed fields
        else if (userRepository.existsByUsername(request.getUsername())) {
            user = userRepository.findByUsername(request.getUsername());

            if (request.getNewUsername() != null &&                            // make sure username is not null
                    !request.getNewUsername().isEmpty() &&                     // make sure username is not empty
                    !request.getNewUsername().equals(user.getUsername())) {    // don't update if not changed
                Optional<User> userOptional = Optional.ofNullable(userRepository.
                        findByUsername(request.getNewUsername()));
                if (userOptional.isPresent()) {                             // check if username is taken
                    throw new IllegalStateException("username taken");
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
                    throw new IllegalStateException("email taken");
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
            return ResponseEntity.badRequest().build();
        }
    }
}
