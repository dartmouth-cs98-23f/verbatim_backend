package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.SubmitGroupChallengeAnswerRequest;
import com.cs98.VerbatimBackend.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private StandardChallengeUserResponseRepository standardChallengeUserResponseRepository;

    @Autowired
    private CustomChallengeUserResponseRepository customChallengeUserResponseRepository;

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

    public List<Question> getStandardChallengeQuestions(GroupChallenge challenge) {
        StandardChallenge standardChallenge = standardChallengeRepository.findByChallenge(challenge);

        if (ObjectUtils.isEmpty(standardChallenge)) { // make sure challenge exists
            throw new RuntimeException("failed to find challenge");
        } else {

            // create a list of all the questions
            List<Question> questions = new ArrayList<>();
            questions.add(standardChallenge.getQ1());
            questions.add(standardChallenge.getQ2());
            questions.add(standardChallenge.getQ3());
            questions.add(standardChallenge.getQ4());
            questions.add(standardChallenge.getQ5());

            return questions;
        }
    }

    public List<CustomQuestion> getCustomChallengeQuestions(GroupChallenge challenge) {
        List<CustomChallenge> customChallenges = customChallengeRepository.findAllByChallenge(challenge);

        if (ObjectUtils.isEmpty(customChallenges)) { // make sure challenge exists
            throw new RuntimeException("failed to find challenge");
        } else {

            // create a list of all the questions
            List<CustomQuestion> questions = new ArrayList<>();
            for (CustomChallenge challengeRow: customChallenges) {
                questions.add(challengeRow.getQuestion());
            }

            return questions;
        }
    }

    public GroupChallengeUserSpecificResponse submitGroupResponse(SubmitGroupChallengeAnswerRequest request) {
        User respondingUser = userRepository.findByUsername(request.getUsername());
        GroupChallenge challenge = groupChallengeRepository.findById(request.getChallengeId());

        if (ObjectUtils.isEmpty(respondingUser)) {
            throw new RuntimeException("could not submit response because user was not found in database");
        }
        if (ObjectUtils.isEmpty(challenge)) {
            throw new RuntimeException("could not submit response because challenge was not found in database");
        }

        // create a standard or custom challenge response
        GroupChallengeUserSpecificResponse response;

        if (challenge.getIsCustom()) {
            if (customChallengeUserResponseRepository.existsByUserIdAndGroupChallengeId(respondingUser.getId(), challenge.getId())) {
                throw new RuntimeException("User has already completed this group challenge");
            }
            response = submitCustomResponse(request);
        } else {
            StandardChallenge standardChallenge = standardChallengeRepository.findByChallenge(challenge);
            if (standardChallengeUserResponseRepository.existsByUserIdAndChallengeId(respondingUser.getId(), standardChallenge.getId())) {
                throw new RuntimeException("User has already completed this group challenge");
            }
            response = submitStandardResponse(request);
        }

        if (ObjectUtils.isEmpty(response)) { // make sure the response is not empty
            throw new RuntimeException("could not build response");
        }

        // update the user's stats
        int chalsCompleted = respondingUser.getNumCustomChallengesCompleted();
        respondingUser.setNumCustomChallengesCompleted(chalsCompleted + 1);
        userRepository.save(respondingUser);

        return response;
    }

    public GroupChallengeUserSpecificResponse submitStandardResponse(SubmitGroupChallengeAnswerRequest request) {
        GroupChallenge challenge = groupChallengeRepository.findById(request.getChallengeId());
        StandardChallenge standardChallenge = standardChallengeRepository
                .findByChallenge(challenge);
        User user = userRepository.findByUsername(request.getUsername());
        List<String> responses = request.getResponses();

        // build the response
        StandardChallengeResponse standardChallengeResponse = StandardChallengeResponse.builder()
                .challenge(standardChallenge)
                .user(user)
                .responseQ1(responses.get(0))
                .responseQ2(responses.get(1))
                .responseQ3(responses.get(2))
                .responseQ4(responses.get(3))
                .responseQ5(responses.get(4))
                .build();

        standardChallengeUserResponseRepository.save(standardChallengeResponse);

        return loadGroupChallengeStatsForUser(user, challenge);
    }

    public GroupChallengeUserSpecificResponse submitCustomResponse(SubmitGroupChallengeAnswerRequest request) {
        GroupChallenge challenge = groupChallengeRepository.findById(request.getChallengeId());
        List<CustomChallenge> customChallenges = customChallengeRepository
                .findAllByChallenge(challenge);
        User user = userRepository.findByUsername(request.getUsername());

        // get the challenge questions
        List<CustomQuestion> questions = getCustomChallengeQuestions(challenge);

        // build the custom challenge responses
        for(int i = 0; i < customChallenges.size(); i++) {
            CustomChallengeResponse customChallengeResponse = CustomChallengeResponse.builder()
                    .challenge(customChallenges.get(i))
                    .groupChallenge(challenge)
                    .user(user)
                    .question(questions.get(i))
                    .response(request.getResponses().get(i))
                    .build();

            customChallengeUserResponseRepository.save(customChallengeResponse);
        }

        return loadGroupChallengeStatsForUser(user, challenge);
    }

    public GroupChallengeUserSpecificResponse loadGroupChallengeForUser(User user, GroupChallenge challenge) {

        // if user has not completed the group challenge
        GroupChallengeUserSpecificResponse response;

        // create an object list to hold the response
        List<Object> questions = new ArrayList<>();

        // if the challenge is custom
        if (challenge.getIsCustom()) {
            questions.add(getCustomChallengeQuestions(challenge));
        }

        // if the challenge is standard
        else {
            questions.add(getStandardChallengeQuestions(challenge));
        }

        // build the response
        response = GroupChallengeUserSpecificResponse
                .builder()
                .groupChallenge(challenge)
                .questions(questions)
                .userHasCompleted(false)
                .build();

        return response;
    }

     public GroupChallengeUserSpecificResponse loadGroupChallengeStatsForUser(User user, GroupChallenge challenge) {
        GroupChallengeUserSpecificResponse response;

        // build response for custom challenge
        if (challenge.getIsCustom()) {

            // get the Custom Challenge rows
            List<CustomChallenge> customChallenges = customChallengeRepository.findAllByChallenge(challenge);

            // get the questions
            List<CustomQuestion> questions = getCustomChallengeQuestions(challenge);

            // get all responses
            List<CustomChallengeResponse> challengeResponses = customChallengeUserResponseRepository
                    .findAllByGroupChallenge(challenge);

            // convert to map for verbamatch
            Map<String, List<String>> userResponses = new HashMap<>();

            // create group answers
            List<GroupAnswers> groupAnswers = new ArrayList<>();
            int totalResponses = 0;

            for (CustomQuestion question: questions) { // loop through questions
                Map<String, String> responseQ = new HashMap<>(); // create the response map
                String content = question.getContent(); // get the question content

                // loop through all responses
                for (CustomChallengeResponse customChallengeResponse: challengeResponses) {

                    // if the response question matches the current question
                    if (customChallengeResponse.getQuestion().getId().equals(question.getId())) {
                        String username = customChallengeResponse.getUser().getUsername();
                        if (!userResponses.containsKey(username)) {
                            // if user not in map, add them
                            List<String> responses = new ArrayList<>();
                            responses.add(customChallengeResponse.getResponse());
                            userResponses.put(username, responses);
                        } else {
                            // add the response
                            List<String> responses = userResponses.get(username);
                            responses.add(customChallengeResponse.getResponse());
                            userResponses.put(username, responses);
                        }
                        // add the response to the response map
                        responseQ.put(customChallengeResponse.getUser().getUsername(),
                                customChallengeResponse.getResponse());
                    }

                    totalResponses = responseQ.size();
                }

                // add the map to the group answers list
                groupAnswers.add(GroupAnswers.builder()
                        .question(content)
                        .responses(responseQ)
                        .build());
            }

            // get verbamatch
            VerbamatchResponse verbamatch = findVerbamatch(userResponses);
            List<String> verbamatchUsers = verbamatch.getUsers();
            double verbamatchScore = verbamatch.getScore();

            response = GroupChallengeUserSpecificResponse.builder()
                    .groupChallenge(challenge)
                    .groupAnswers(groupAnswers)
                    .totalResponses(totalResponses)
                    .userHasCompleted(true)
                    .verbaMatch(verbamatchUsers)
                    .verbaMatchSimilarity(verbamatchScore)
                    .build();

        } else { // build response for standard challenge

            // get the Standard Challenge
            StandardChallenge standardChallenge = standardChallengeRepository.findByChallenge(challenge);

            // get the questions
            List<Question> questions = getStandardChallengeQuestions(challenge);

            // get all responses
            List<StandardChallengeResponse> challengeResponses = standardChallengeUserResponseRepository
                    .findAllByChallenge(standardChallenge);

            // convert to map for verbamatch
            Map<String, List<String>> userResponses = new HashMap<>();

            // create the maps to hold answers
            Map<String, String> responseQ1 = new HashMap<>();
            Map<String, String> responseQ2 = new HashMap<>();
            Map<String, String> responseQ3 = new HashMap<>();
            Map<String, String> responseQ4 = new HashMap<>();
            Map<String, String> responseQ5 = new HashMap<>();

            // add responses to the maps
            for (StandardChallengeResponse challengeResponse: challengeResponses) {
                List<String> responses = new ArrayList<>();
                String username = challengeResponse.getUser().getUsername();
                responseQ1.put(username, challengeResponse.getResponseQ1());
                responseQ2.put(username, challengeResponse.getResponseQ2());
                responseQ3.put(username, challengeResponse.getResponseQ3());
                responseQ4.put(username, challengeResponse.getResponseQ4());
                responseQ5.put(username, challengeResponse.getResponseQ5());
                responses.add(challengeResponse.getResponseQ1());
                responses.add(challengeResponse.getResponseQ2());
                responses.add(challengeResponse.getResponseQ3());
                responses.add(challengeResponse.getResponseQ4());
                responses.add(challengeResponse.getResponseQ5());
                userResponses.put(username, responses);
            }

            // get verbamatch
            VerbamatchResponse verbamatch = findVerbamatch(userResponses);
            List<String> verbamatchUsers = verbamatch.getUsers();
            double verbamatchScore = verbamatch.getScore();

            // create Group Answers
            List<GroupAnswers> groupAnswers = new ArrayList<>();
            groupAnswers.add(GroupAnswers.builder()
                    .question(String.valueOf(questions.get(0).getContent()))
                    .responses(responseQ1)
                    .build());
            groupAnswers.add(GroupAnswers.builder()
                    .question(String.valueOf(questions.get(1).getContent()))
                    .responses(responseQ2)
                    .build());
            groupAnswers.add(GroupAnswers.builder()
                    .question(String.valueOf(questions.get(2).getContent()))
                    .responses(responseQ3)
                    .build());
            groupAnswers.add(GroupAnswers.builder()
                    .question(String.valueOf(questions.get(3).getContent()))
                    .responses(responseQ4)
                    .build());
            groupAnswers.add(GroupAnswers.builder()
                    .question(String.valueOf(questions.get(4).getContent()))
                    .responses(responseQ5)
                    .build());

            int totalResponses = challengeResponses.size();

            response = GroupChallengeUserSpecificResponse.builder()
                    .groupChallenge(challenge)
                    .groupAnswers(groupAnswers)
                    .totalResponses(totalResponses)
                    .userHasCompleted(true)
                    .verbaMatch(verbamatchUsers)
                    .verbaMatchSimilarity(verbamatchScore)
                    .build();
        }

        if (ObjectUtils.isEmpty(response)) { // make sure the response is not empty
            throw new RuntimeException("could not build response");
        }

        return response;
    }

    public static VerbamatchResponse findVerbamatch(Map<String, List<String>> userResponses) {


        if (userResponses.size() < 2) {
            return VerbamatchResponse.builder()
                    .users(new ArrayList<>())
                    .score(0.0)
                    .build();
        }
        // List to store the similarity score for each user pair in a challenge
        Map<List<String>, Double> similarityScoresList = new HashMap<>();
        List<String> usernames = new ArrayList<>();
        List<List<String>> responses = new ArrayList<>();

        // put responses into lists that are indexable
        for (Map.Entry<String, List<String>> entry : userResponses.entrySet()) {
            usernames.add(entry.getKey());
            responses.add(entry.getValue());
        }

        // iterate through responses
        for (int i = 0; i < usernames.size(); i++) {
            for (int j = 1; j < usernames.size(); j++) {
                if (i != j) { // make sure we don't calculate similarity between the same user
                    List<String> users = new ArrayList<>();
                    users.add(usernames.get(i));
                    users.add(usernames.get(j));

                    // calculate the similarity for the users' responses
                    similarityScoresList.put(users, jaccardSimilarity(responses.get(i), responses.get(j)));
                }
            }
        }

        // get the maximum similarity
        List<String> maxUsers = Collections.max(similarityScoresList.entrySet(), Map.Entry.comparingByValue()).getKey();
        Double maxScore = similarityScoresList.get(maxUsers);

        VerbamatchResponse response = VerbamatchResponse.builder()
                .users(maxUsers)
                .score(maxScore)
                .build();

        return response;
    }

    public static double jaccardSimilarity(List<String> responses1, List<String> responses2) {
        // Calculate Jaccard similarity between two sets of responses
        int intersection = 0;
        int union = 0;

        for (int i = 0; i < responses1.size(); i++) {
            if (responses1.get(i).equals(responses2.get(i))) {
                intersection++;
            }
            union++;
        }

        // calculate raw similarity
        double rawSimilarity = (double) intersection / union;

        // normalize to 100 scale
        double finalSimilarity = rawSimilarity  * 100;

        // calculate final rounded similarity
        return Math.round(finalSimilarity * 100.0) / 100.0;
    }
}
