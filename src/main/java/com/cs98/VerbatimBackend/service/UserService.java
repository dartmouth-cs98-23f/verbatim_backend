package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.misc.UserDetailsPrincipal;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetailsPrincipal loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User userToAuthenticate;
        if (ObjectUtils.isEmpty(emailOrUsername)) {
            return null;
        }

        if (userRepository.existsByEmail(emailOrUsername)) {
            userToAuthenticate = userRepository.findByEmail(emailOrUsername);
        }
        else if (userRepository.existsByUsername(emailOrUsername)) {
            userToAuthenticate = userRepository.findByUsername(emailOrUsername);
        }
        else {
            return null;
        }

        return new UserDetailsPrincipal(userToAuthenticate);

    }

}
