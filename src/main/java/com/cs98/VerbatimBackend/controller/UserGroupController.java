package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.UserGroupCreationRequest;
import com.cs98.VerbatimBackend.response.UserGroupCreationResponse;
import com.cs98.VerbatimBackend.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGroupController {


    @Autowired
    UserGroupService userGroupService;

    @PostMapping(path = "api/v1/createGroup")
    public ResponseEntity<UserGroupCreationResponse> createGroup(@RequestBody UserGroupCreationRequest request) {
        if (ObjectUtils.isEmpty(request.getCreatedByUsername())) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        UserGroupCreationResponse response = userGroupService.createGroup(request);

        if (ObjectUtils.isEmpty(response)) {
            return ResponseEntity.status(Status.GROUP_CREATION_FAILED).build();
        }

        return ResponseEntity.ok(response);

    }
}
