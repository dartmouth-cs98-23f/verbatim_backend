package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.response.CreateCustomChallengeResponse;
import com.cs98.VerbatimBackend.response.CreateStandardChallengeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupChallengeService {

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

    @Autowired
    private CustomQuestionRepository customQuestionRepository;

    @Autowired
    private CustomChallengeRepository customChallengeRepository;

    public GroupChallenge createGroupChallenge(User createdByUser, UserGroup group, boolean isCustom) {

        // create the group challenge
        GroupChallenge groupChallenge = GroupChallenge.builder()
                .createdBy(createdByUser)
                .group(group)
                .isCustom(isCustom)
                .date(Date.valueOf(LocalDate.now()))
                .isActive(true)
                .build();

        // save the group challenge
        groupChallengeRepository.save(groupChallenge);

        return groupChallenge;
    }

    public CreateStandardChallengeResponse createStandardChallenge(GroupChallenge groupChallenge) {

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

        return CreateStandardChallengeResponse.builder()
                .groupChallenge(groupChallenge)
                .questionList(standardQuestions)
                .build();


    }

    public CreateCustomChallengeResponse createCustomChallenge(GroupChallenge groupChallenge, List<String> questions) {

        // for each question, create a CustomQuestion and add it to a list
        List<CustomQuestion> customQuestions = new ArrayList<>();
        for (String question: questions) {
            CustomQuestion customQuestion = CustomQuestion.builder()
                    .content(question)
                    .build();

            customQuestionRepository.save(customQuestion);
            customQuestions.add(customQuestion);
        }

        // add each CustomQuestion to a CustomChallenge
        for (CustomQuestion question: customQuestions) {
            CustomChallenge customChallenge = CustomChallenge.builder()
                    .challenge(groupChallenge)
                    .question(question)
                    .build();

            customChallengeRepository.save(customChallenge);
        }
        return CreateCustomChallengeResponse.builder()
                .groupChallenge(groupChallenge)
                .questionList(customQuestions)
                .build();
    }
}
