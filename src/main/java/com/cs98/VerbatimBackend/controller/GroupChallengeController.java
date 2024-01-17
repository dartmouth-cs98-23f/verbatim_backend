package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.CustomChallengeRequest;
import com.cs98.VerbatimBackend.request.StandardChallengeRequest;
import com.cs98.VerbatimBackend.request.SubmitGroupChallengeAnswerRequest;
import com.cs98.VerbatimBackend.response.CreateCustomChallengeResponse;
import com.cs98.VerbatimBackend.response.CreateStandardChallengeResponse;
import com.cs98.VerbatimBackend.response.GlobalChallengeUserSpecificResponse;
import com.cs98.VerbatimBackend.response.GroupChallengeUserSpecificResponse;
import com.cs98.VerbatimBackend.service.GroupChallengeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("api/v1/createStandardChallenge")
    public ResponseEntity<CreateStandardChallengeResponse> createStandardChallenge(@RequestBody StandardChallengeRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());
        UserGroup group = userGroupRepository.findById(request.getGroupId());
        CreateStandardChallengeResponse response;

        // TODO: check if createdByUser is in group

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

    @PostMapping("api/v1/getChallengeQs")
    public ResponseEntity<List<Object>> getChallengeQs(@RequestBody int challengeId) {
        GroupChallenge groupChallenge;

        // check that the challenge exists
        if (ObjectUtils.isEmpty(groupChallengeRepository.findById(challengeId))) {
            return ResponseEntity.status(Status.GROUP_CHALLENGE_NOT_FOUND).build();
        } else {
            groupChallenge = groupChallengeRepository.findById(challengeId);
        }

        // create an object list to hold the response
        List<Object> response = new ArrayList<>();

        if (groupChallenge.getIsCustom()) {
            response.add(groupChallengeService.getCustomChallengeQuestions(groupChallenge));
        } else {
            response.add(groupChallengeService.getStandardChallengeQuestions(groupChallenge));
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
}