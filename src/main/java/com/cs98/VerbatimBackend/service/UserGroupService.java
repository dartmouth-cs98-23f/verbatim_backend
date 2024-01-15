package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroup;
import com.cs98.VerbatimBackend.model.UserGroupJunction;
import com.cs98.VerbatimBackend.repository.UserGroupJunctionRepository;
import com.cs98.VerbatimBackend.repository.UserGroupRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.UserGroupCreationRequest;
import com.cs98.VerbatimBackend.response.UserGroupCreationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.net.ssl.SSLEngineResult;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserGroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupJunctionRepository userGroupJunctionRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public UserGroupCreationResponse createGroup(UserGroupCreationRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());

        UserGroup group = UserGroup.builder()
                .name(request.getGroupName())
                .build();

        userGroupRepository.save(group);

        UserGroupJunction createdByGroupMember = UserGroupJunction.builder()
                .group(group)
                .user(createdByUser)
                .build();

        userGroupJunctionRepository.save(createdByGroupMember);

        List<User> usersToAdd = new ArrayList<>();

        for (String username : request.getUsernamesToAdd()) {
            User userToAdd = userRepository.findByUsername(username);

            if (ObjectUtils.isEmpty(userToAdd)) {
                return null;
            }
            UserGroupJunction groupMember = UserGroupJunction.builder()
                    .user(userToAdd)
                    .group(group)
                    .build();

            userGroupJunctionRepository.save(groupMember);
            usersToAdd.add(userToAdd);
        }

        return UserGroupCreationResponse.builder()
                .users(usersToAdd)
                .groupName(group.getName())
                .groupId(group.getId())
                .build();
    }
}
