package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.CustomChallengeRequest;
import com.cs98.VerbatimBackend.request.StandardChallengeRequest;
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

@RestController
public class GroupChallengeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupChallengeRepository groupChallengeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StandardChallengeRepository standardChallengeRepository;

    @PostMapping("api/v1/createStandardChallenge")
    public ResponseEntity<List<Question>> createStandardChallenge(@RequestBody StandardChallengeRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());
        UserGroup group = userGroupRepository.findByName(request.getGroupName());

        // TODO: check if createdByUser is in group

        if (ObjectUtils.isEmpty(createdByUser)) { // check that user exists
            return ResponseEntity.status(Status.USER_NOT_FOUND).build();
        }

        if (ObjectUtils.isEmpty(group)) { // check that group exists
            return ResponseEntity.status(Status.GROUP_NOT_FOUND).build();
        }

        // create a group challenge
        GroupChallenge groupChallenge = GroupChallenge.builder()
                .createdBy(createdByUser)
                .group(group)
                .isCustom(false)
                .build();

        // save the group challenge
        groupChallengeRepository.save(groupChallenge);

        // get five random categories
        List<Category> categories = categoryRepository.fetchFiveRandomCategories();

        // get a random question from each category
        List<Question> standardQuestions = new ArrayList<>();
        for (Category category : categories) {
            Question question = questionRepository.fetchRandomQuestionByCategoryId(category.getId());
            standardQuestions.add(question);
        }

        // create a standard challenge
        StandardChallenge standardChallenge = StandardChallenge.builder()
                .challenge(groupChallenge)
                .date(Date.valueOf(LocalDate.now()))
                .q1(standardQuestions.get(0))
                .q2(standardQuestions.get(1))
                .q3(standardQuestions.get(2))
                .q4(standardQuestions.get(3))
                .q5(standardQuestions.get(4))
                .build();

        // save the standard challenge
        standardChallengeRepository.save(standardChallenge);

        return ResponseEntity.ok(standardQuestions);
    }

    @PostMapping("api/v1/createCustomChallenge")
    public ResponseEntity<String> createStandardChallenge(@RequestBody CustomChallengeRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());
        // placeholder - TODO
        return null;
    }
}
