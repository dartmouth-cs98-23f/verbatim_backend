package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroupJunction;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.GroupInviteRequest;
import com.cs98.VerbatimBackend.request.LeaveGroupRequest;
import com.cs98.VerbatimBackend.request.UserGroupCreationRequest;
import com.cs98.VerbatimBackend.response.GetActiveChallengesResponse;
import com.cs98.VerbatimBackend.response.GetGroupsResponse;
import com.cs98.VerbatimBackend.response.GroupStats;
import com.cs98.VerbatimBackend.response.UserGroupCreationResponse;
import com.cs98.VerbatimBackend.service.UserGroupService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserGroupController {


    @Autowired
    UserGroupService userGroupService;

    @Autowired
    UserRepository userRepository;

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

    @GetMapping(path = "api/v1/user/{username}/groups")
    public ResponseEntity<GetGroupsResponse> getMyGroups(@PathVariable String username) {
        if (ObjectUtils.isEmpty(username)) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        GetGroupsResponse response = userGroupService.findGroupsForUsername(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "api/v1/inviteToGroup")
    public ResponseEntity<String> inviteToGroup(@RequestBody GroupInviteRequest request) {
        int status = userGroupService.inviteUser(request);
        if (status == Status.OK) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(Status.USER_ADDED_TO_GROUP_FAILED).build();

    }

    @PostMapping(path = "api/v1/leaveGroup")
    public ResponseEntity<String> leaveGroup(@RequestBody LeaveGroupRequest request) {
        int status = userGroupService.removeUserFromGroup(request);

        if (status == Status.OK) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(status).build();
    }

    @GetMapping(path = "api/v1/group/{groupId}/challenges")
    public ResponseEntity<GetActiveChallengesResponse> getActiveChallenges(@PathVariable int groupId) {
        GetActiveChallengesResponse challenges = userGroupService.getActiveChallenges(groupId);

        if (ObjectUtils.isEmpty(challenges)) {
            return ResponseEntity.status(Status.GROUP_CHALLENGE_NOT_FOUND).build();
        }
        return ResponseEntity.ok(challenges);
    }

    @GetMapping(path = "api/v1/group/{groupId}")
    public ResponseEntity<GroupStats> getGroupStats(@PathVariable int groupId) {
        GroupStats stats = userGroupService.getStats(groupId);

        if (ObjectUtils.isEmpty(stats)) {
            return ResponseEntity.status(Status.FETCH_GROUP_STATS_FAILED).build();
        }

        return ResponseEntity.ok(stats);

    }


}
