package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.CustomChallengeRequest;
import com.cs98.VerbatimBackend.request.StandardChallengeRequest;
import com.cs98.VerbatimBackend.request.SubmitGroupChallengeAnswerRequest;
import com.cs98.VerbatimBackend.response.*;
import com.cs98.VerbatimBackend.service.GroupChallengeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class GroupChallengeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupChallengeRepository groupChallengeRepository;

    @Autowired
    private GroupChallengeService groupChallengeService;

    @Autowired
    private UserGroupJunctionRepository userGroupJunctionRepository;

    @Autowired
    private StandardChallengeUserResponseRepository standardChallengeUserResponseRepository;

    @Autowired
    private CustomChallengeUserResponseRepository customChallengeUserResponseRepository;

    @Autowired StandardChallengeRepository standardChallengeRepository;

    @PostMapping("api/v1/createStandardChallenge")
    public ResponseEntity<CreateStandardChallengeResponse> createStandardChallenge(@RequestBody StandardChallengeRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());
        UserGroup group = userGroupRepository.findById(request.getGroupId());
        CreateStandardChallengeResponse response;

        // check that user is in group
        if (!userGroupJunctionRepository.existsByGroupAndUser(group, createdByUser)) {
            return ResponseEntity.status(Status.USER_NOT_IN_GROUP).build();
        }

        if (ObjectUtils.isEmpty(createdByUser)) { // check that user exists
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (ObjectUtils.isEmpty(group)) { // check that group exists
            return ResponseEntity.status(Status.GROUP_NOT_FOUND).build();
        }

        // create a group challenge
        GroupChallenge groupChallenge = groupChallengeService
                .createGroupChallenge(createdByUser, group, false);

        // create a standard challenge
        response = groupChallengeService.createStandardChallenge(groupChallenge);

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("api/v1/createCustomChallenge")
    public ResponseEntity<CreateCustomChallengeResponse> createCustomChallenge(@RequestBody CustomChallengeRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());
        UserGroup group = userGroupRepository.findById(request.getGroupId());
        CreateCustomChallengeResponse response;

        if (ObjectUtils.isEmpty(createdByUser)) { // check that user exists
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (ObjectUtils.isEmpty(group)) { // check that group exists
            return ResponseEntity.status(Status.GROUP_NOT_FOUND).build();
        }

        // create a group challenge
        GroupChallenge groupChallenge = groupChallengeService
                .createGroupChallenge(createdByUser, group, true);

        // create a custom challenge
        response = groupChallengeService
                .createCustomChallenge(groupChallenge, request.getQuestions());

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("api/v1/{challengeId}/{username}/getChallengeQs")
    public ResponseEntity<GroupChallengeUserSpecificResponse> getChallengeQs(@PathVariable int challengeId,
                                                                             @PathVariable String username) {
        GroupChallenge groupChallenge;
        User user;
        GroupChallengeUserSpecificResponse response;

        // check that the challenge exists
        if (ObjectUtils.isEmpty(groupChallengeRepository.findById(challengeId))) {
            return ResponseEntity.status(Status.GROUP_CHALLENGE_NOT_FOUND).build();
        } else {
            groupChallenge = groupChallengeRepository.findById(challengeId);
        }

        // check that the user exists
        if (ObjectUtils.isEmpty(userRepository.findByUsername(username))) {
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        } else {
            user = userRepository.findByUsername(username);
        }

        boolean hasCompleted;
        if (groupChallenge.getIsCustom()) {
            hasCompleted = customChallengeUserResponseRepository.existsByUserIdAndGroupChallengeId(user.getId(), challengeId);
            if (hasCompleted) {
                response = groupChallengeService.loadGroupChallengeStatsForUser(user, groupChallenge);
            } else {
                response = groupChallengeService.loadGroupChallengeForUser(user, groupChallenge);
            }
        } else {
            StandardChallenge standardChallenge = standardChallengeRepository.findByChallenge(groupChallenge);
            hasCompleted = standardChallengeUserResponseRepository.existsByUserIdAndChallengeId(user.getId(), standardChallenge.getId());
            if (hasCompleted) {
                response = groupChallengeService.loadGroupChallengeStatsForUser(user, groupChallenge);
            } else {
                response = groupChallengeService.loadGroupChallengeForUser(user, groupChallenge);
            }
        }

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("api/v1/submitGroupResponse")
    public ResponseEntity<GroupChallengeUserSpecificResponse> submitGroupChallengeResponse(
            @NotNull @RequestBody SubmitGroupChallengeAnswerRequest request) {

        User user = userRepository.findByUsername(request.getUsername());
        GroupChallenge challenge = groupChallengeRepository.findById(request.getChallengeId());

        if (ObjectUtils.isEmpty(user)) { // make sure user exists
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (ObjectUtils.isEmpty(challenge)) { // make sure challenge exists
            return ResponseEntity.status(Status.GROUP_CHALLENGE_NOT_FOUND).build();
        }

        GroupChallengeUserSpecificResponse response = groupChallengeService.submitGroupResponse(request);

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("api/v1/{challengeId}/{username}/userHasCompleted")
    public ResponseEntity<Boolean> userHasCompletedChallenge(@PathVariable int challengeId,
                                                             @PathVariable String username) {
        User user = userRepository.findByUsername(username);
        GroupChallenge challenge = groupChallengeRepository.findById(challengeId);

        if (ObjectUtils.isEmpty(user)) { // make sure user exists
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (ObjectUtils.isEmpty(challenge)) { // make sure challenge exists
            return ResponseEntity.status(Status.GROUP_CHALLENGE_NOT_FOUND).build();
        }

        boolean hasCompleted;
        if (challenge.getIsCustom()) {
            hasCompleted = customChallengeUserResponseRepository.existsByUserIdAndGroupChallengeId(user.getId(), challengeId);
        } else {
            StandardChallenge standardChallenge = standardChallengeRepository.findByChallenge(challenge);
            hasCompleted = standardChallengeUserResponseRepository.existsByUserIdAndChallengeId(user.getId(), standardChallenge.getId());
        }

        return ResponseEntity.ok(hasCompleted);

    }

}
