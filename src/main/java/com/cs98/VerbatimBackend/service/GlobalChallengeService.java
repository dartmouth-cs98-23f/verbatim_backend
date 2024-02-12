package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeUserResponseRepository;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequest;
import com.cs98.VerbatimBackend.response.GlobalChallengeUserSpecificResponse;
import com.cs98.VerbatimBackend.response.QuestionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlobalChallengeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Autowired
    private GlobalChallengeUserResponseRepository globalChallengeUserResponseRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    public GlobalChallengeUserSpecificResponse submitGlobalResponse(SubmitGlobalChallengeAnswerRequest submissionRequest) {
        User respondingUser = userRepository.findByUsername(submissionRequest.getUsername());
        if (ObjectUtils.isEmpty(respondingUser)) {
            throw new RuntimeException("could not submit response because user was not found in database");
        }

        GlobalChallenge challenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();
        if (ObjectUtils.isEmpty(challenge)) {
            throw new RuntimeException("failed to find global challenge in database");
        }
        if (globalChallengeUserResponseRepository.existsByUserIdAndGlobalChallengeId(respondingUser.getId(), challenge.getId())) {
            throw new RuntimeException("User has already completed the daily challenge");
        }

        respondingUser.setStreak(respondingUser.getStreak() + 1);
        respondingUser.setNumGlobalChallengesCompleted(respondingUser.getNumGlobalChallengesCompleted() + 1);
        respondingUser.setHasCompletedDailyChallenge(true);
        userRepository.save(respondingUser);

        GlobalChallengeUserResponse userResponseDAO = GlobalChallengeUserResponse.builder()
                .globalChallenge(challenge)
                .user(respondingUser)
                .responseQ1(submissionRequest.getResponseQ1())
                .responseQ2(submissionRequest.getResponseQ2())
                .responseQ3(submissionRequest.getResponseQ3())
                .responseQ4(submissionRequest.getResponseQ4())
                .responseQ5(submissionRequest.getResponseQ5())
                .build();

        globalChallengeUserResponseRepository.save(userResponseDAO);

        return loadGlobalChallengeStatsForUser(respondingUser, userResponseDAO);

    }

    public GlobalChallengeUserSpecificResponse loadGlobalChallengeStatsForUser(User user, GlobalChallengeUserResponse response) {

        int totalResponses = globalChallengeUserResponseRepository.countByGlobalChallengeId(response.getGlobalChallenge().getId());

        int numVerbatimQ1 = globalChallengeUserResponseRepository.
                countByResponseQ1AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ1(),
                        response.getGlobalChallenge().getId(),
                        user.getId());

        int numVerbatimQ2 = globalChallengeUserResponseRepository.
                countByResponseQ2AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ2(),
                        response.getGlobalChallenge().getId(),
                        user.getId());

        int numVerbatimQ3 = globalChallengeUserResponseRepository.
                countByResponseQ3AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ3(),
                        response.getGlobalChallenge().getId(),
                        user.getId());

        int numVerbatimQ4 = globalChallengeUserResponseRepository.
                countByResponseQ4AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ4(),
                        response.getGlobalChallenge().getId(),
                        user.getId());

        int numVerbatimQ5 = globalChallengeUserResponseRepository.
                countByResponseQ5AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ5(),
                        response.getGlobalChallenge().getId(),
                        user.getId());

        int numExactVerbatim = globalChallengeUserResponseRepository.
                countByResponseQ1AndResponseQ2AndResponseQ3AndResponseQ4AndResponseQ5AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ1(),
                        response.getResponseQ2(),
                        response.getResponseQ3(),
                        response.getResponseQ4(),
                        response.getResponseQ5(),
                        response.getGlobalChallenge().getId(),
                        user.getId()
                );

        // figure out which question has the most verbatims
        int mostVerbatim = Math.max(Math.max(Math.max(Math.max(
                numVerbatimQ1, numVerbatimQ2), numVerbatimQ3), numVerbatimQ4), numVerbatimQ5);

        // create an empty list of usernames
        Map<String, List<String>> verbatastic = new HashMap<String, List<String>>();
        List<String> usersVerbatim = new ArrayList<String>();
        List<GlobalChallengeUserResponse> submissions;
        String responseVerbatim;

        // for the question with the most verbatims, get all global challenge submissions with matching response
        if (mostVerbatim == numVerbatimQ1) {
            responseVerbatim = response.getResponseQ1();
            submissions = globalChallengeUserResponseRepository.
                    findAllByGlobalChallengeIdAndResponseQ1(response.getGlobalChallenge().getId(), responseVerbatim);
        } else if (mostVerbatim == numVerbatimQ2) {
            responseVerbatim = response.getResponseQ2();
            submissions = globalChallengeUserResponseRepository.
                    findAllByGlobalChallengeIdAndResponseQ2(response.getGlobalChallenge().getId(), responseVerbatim);
        } else if (mostVerbatim == numVerbatimQ3){
            responseVerbatim = response.getResponseQ3();
            submissions = globalChallengeUserResponseRepository.
                    findAllByGlobalChallengeIdAndResponseQ3(response.getGlobalChallenge().getId(), responseVerbatim);
        } else if (mostVerbatim == numVerbatimQ4) {
            responseVerbatim = response.getResponseQ4();
            submissions = globalChallengeUserResponseRepository.
                    findAllByGlobalChallengeIdAndResponseQ4(response.getGlobalChallenge().getId(), responseVerbatim);
        } else {
            responseVerbatim = response.getResponseQ5();
            submissions = globalChallengeUserResponseRepository.
                    findAllByGlobalChallengeIdAndResponseQ5(response.getGlobalChallenge().getId(), responseVerbatim);
        }

        // add all usernames except for the current user to usersVerbatim
        for (GlobalChallengeUserResponse submission: submissions) {
            if (!submission.getUser().getUsername().equals(user.getUsername())) {
                usersVerbatim.add(submission.getUser().getUsername());
            }
        }
        verbatastic.put(responseVerbatim, usersVerbatim);

        // make a list of the user's active friends' IDs
        List<UserRelationship> activeFriends = userRelationshipRepository.findActiveFriendsByUserId(user.getId());
        List<Integer> friendsList = new ArrayList<>();
        for (UserRelationship relationship : activeFriends) {
            if (relationship.getRequestingFriend().getId().equals(user.getId())) {
                friendsList.add(relationship.getRequestedFriend().getId());
            }
            else {
                friendsList.add(relationship.getRequestingFriend().getId());
            }
        }

        // get all global challenge responses for the given ID and the user's friends
        List<GlobalChallengeUserResponse> friendResponses = globalChallengeUserResponseRepository
                .findByGlobalChallengeIdAndUserIdIn(response.getGlobalChallenge().getId(), friendsList);

        // define maps for each question
        Map<String, String> q1FriendResponses = new HashMap<String, String>();
        Map<String, String> q2FriendResponses = new HashMap<String, String>();
        Map<String, String> q3FriendResponses = new HashMap<String, String>();
        Map<String, String> q4FriendResponses = new HashMap<String, String>();
        Map<String, String> q5FriendResponses = new HashMap<String, String>();

        // for each friend's global challenge response, map their username to their response
        for (GlobalChallengeUserResponse friendResponse: friendResponses) {
            String friend = friendResponse.getUser().getUsername();
            String responseQ1 = friendResponse.getResponseQ1();
            String responseQ2 = friendResponse.getResponseQ2();
            String responseQ3 = friendResponse.getResponseQ3();
            String responseQ4 = friendResponse.getResponseQ4();
            String responseQ5 = friendResponse.getResponseQ5();

            q1FriendResponses.put(friend, responseQ1);
            q2FriendResponses.put(friend, responseQ2);
            q3FriendResponses.put(friend, responseQ3);
            q4FriendResponses.put(friend, responseQ4);
            q5FriendResponses.put(friend, responseQ5);
        }

        String firstMostPopularQ1 = globalChallengeUserResponseRepository.findMostPopularQ1ResponseByChallengeId(
                response.getGlobalChallenge().getId()
        );
        String secondMostPopularQ1 = globalChallengeUserResponseRepository.findSecondMostPopularQ1ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ1
        );
        String thirdMostPopularQ1 = globalChallengeUserResponseRepository.findThirdMostPopularQ1ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ1,
                secondMostPopularQ1
        );

        QuestionStatistics statsQ1 = QuestionStatistics.builder()
                .firstMostPopular(firstMostPopularQ1)
                .secondMostPopular(secondMostPopularQ1)
                .thirdMostPopular(thirdMostPopularQ1)
                .numResponsesFirst(globalChallengeUserResponseRepository.countByResponseQ1AndGlobalChallengeId(
                        firstMostPopularQ1,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesSecond(globalChallengeUserResponseRepository.countByResponseQ1AndGlobalChallengeId(
                        secondMostPopularQ1,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesThird(globalChallengeUserResponseRepository.countByResponseQ1AndGlobalChallengeId(
                        thirdMostPopularQ1,
                        response.getGlobalChallenge().getId()
                ))
                .friendResponses(q1FriendResponses)
                .build();

        String firstMostPopularQ2 = globalChallengeUserResponseRepository.findMostPopularQ2ResponseByChallengeId(
                response.getGlobalChallenge().getId()
        );

        String secondMostPopularQ2 = globalChallengeUserResponseRepository.findSecondMostPopularQ2ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ2
        );

        String thirdMostPopularQ2 = globalChallengeUserResponseRepository.findThirdMostPopularQ2ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ2,
                secondMostPopularQ2
        );

        QuestionStatistics statsQ2 = QuestionStatistics.builder()
                .firstMostPopular(firstMostPopularQ2)
                .secondMostPopular(secondMostPopularQ2)
                .thirdMostPopular(thirdMostPopularQ2)
                .numResponsesFirst(globalChallengeUserResponseRepository.countByResponseQ2AndGlobalChallengeId(
                        firstMostPopularQ2,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesSecond(globalChallengeUserResponseRepository.countByResponseQ2AndGlobalChallengeId(
                        secondMostPopularQ2,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesThird(globalChallengeUserResponseRepository.countByResponseQ2AndGlobalChallengeId(
                        thirdMostPopularQ2,
                        response.getGlobalChallenge().getId()
                ))
                .friendResponses(q2FriendResponses)
                .build();

        String firstMostPopularQ3 = globalChallengeUserResponseRepository.findMostPopularQ3ResponseByChallengeId(
                response.getGlobalChallenge().getId()
        );
        String secondMostPopularQ3 = globalChallengeUserResponseRepository.findSecondMostPopularQ3ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ3
        );
        String thirdMostPopularQ3 = globalChallengeUserResponseRepository.findThirdMostPopularQ3ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ3,
                secondMostPopularQ3
        );

        QuestionStatistics statsQ3 = QuestionStatistics.builder()
                .firstMostPopular(firstMostPopularQ3)
                .secondMostPopular(secondMostPopularQ3)
                .thirdMostPopular(thirdMostPopularQ3)
                .numResponsesFirst(globalChallengeUserResponseRepository.countByResponseQ3AndGlobalChallengeId(
                        firstMostPopularQ3,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesSecond(globalChallengeUserResponseRepository.countByResponseQ3AndGlobalChallengeId(
                        secondMostPopularQ3,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesThird(globalChallengeUserResponseRepository.countByResponseQ3AndGlobalChallengeId(
                        thirdMostPopularQ3,
                        response.getGlobalChallenge().getId()
                ))
                .friendResponses(q3FriendResponses)
                .build();

        String firstMostPopularQ4 = globalChallengeUserResponseRepository.findMostPopularQ4ResponseByChallengeId(
                response.getGlobalChallenge().getId()
        );
        String secondMostPopularQ4 = globalChallengeUserResponseRepository.findSecondMostPopularQ4ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ4
        );
        String thirdMostPopularQ4 = globalChallengeUserResponseRepository.findThirdMostPopularQ4ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ4,
                secondMostPopularQ4
        );

        QuestionStatistics statsQ4 = QuestionStatistics.builder()
                .firstMostPopular(firstMostPopularQ4)
                .secondMostPopular(secondMostPopularQ4)
                .thirdMostPopular(thirdMostPopularQ4)
                .numResponsesFirst(globalChallengeUserResponseRepository.countByResponseQ4AndGlobalChallengeId(
                        firstMostPopularQ4,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesSecond(globalChallengeUserResponseRepository.countByResponseQ4AndGlobalChallengeId(
                        secondMostPopularQ4,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesThird(globalChallengeUserResponseRepository.countByResponseQ4AndGlobalChallengeId(
                        thirdMostPopularQ4,
                        response.getGlobalChallenge().getId()
                ))
                .friendResponses(q4FriendResponses)
                .build();

        String firstMostPopularQ5 = globalChallengeUserResponseRepository.findMostPopularQ5ResponseByChallengeId(
                response.getGlobalChallenge().getId()
        );
        String secondMostPopularQ5 = globalChallengeUserResponseRepository.findSecondMostPopularQ5ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ5
        );
        String thirdMostPopularQ5 = globalChallengeUserResponseRepository.findThirdMostPopularQ5ResponseByChallengeId(
                response.getGlobalChallenge().getId(),
                firstMostPopularQ5,
                secondMostPopularQ5
        );

        QuestionStatistics statsQ5 = QuestionStatistics.builder()
                .firstMostPopular(firstMostPopularQ5)
                .secondMostPopular(secondMostPopularQ5)
                .thirdMostPopular(thirdMostPopularQ5)
                .numResponsesFirst(globalChallengeUserResponseRepository.countByResponseQ5AndGlobalChallengeId(
                        firstMostPopularQ5,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesSecond(globalChallengeUserResponseRepository.countByResponseQ5AndGlobalChallengeId(
                        secondMostPopularQ5,
                        response.getGlobalChallenge().getId()
                ))
                .numResponsesThird(globalChallengeUserResponseRepository.countByResponseQ5AndGlobalChallengeId(
                        thirdMostPopularQ5,
                        response.getGlobalChallenge().getId()
                ))
                .friendResponses(q5FriendResponses)
                .build();

        return GlobalChallengeUserSpecificResponse.builder()
                .q1(response.getGlobalChallenge().getQ1().getContent())
                .q2(response.getGlobalChallenge().getQ2().getContent())
                .q3(response.getGlobalChallenge().getQ3().getContent())
                .q4(response.getGlobalChallenge().getQ4().getContent())
                .q5(response.getGlobalChallenge().getQ5().getContent())
                .globalChallengeId(response.getGlobalChallenge().getId())
                .globalChallengeDisplayNum(globalChallengeRepository.getGlobalChallengeDisplayNumber())
                .categoryQ1(response.getGlobalChallenge().getQ1().getCategory().getTitle())
                .categoryQ2(response.getGlobalChallenge().getQ2().getCategory().getTitle())
                .categoryQ3(response.getGlobalChallenge().getQ3().getCategory().getTitle())
                .categoryQ4(response.getGlobalChallenge().getQ4().getCategory().getTitle())
                .categoryQ5(response.getGlobalChallenge().getQ5().getCategory().getTitle())
                .responseQ1(response.getResponseQ1())
                .responseQ2(response.getResponseQ2())
                .responseQ3(response.getResponseQ3())
                .responseQ4(response.getResponseQ4())
                .responseQ5(response.getResponseQ5())
                .totalResponses(totalResponses)
                .numVerbatimQ1(numVerbatimQ1)
                .numVerbatimQ2(numVerbatimQ2)
                .numVerbatimQ3(numVerbatimQ3)
                .numVerbatimQ4(numVerbatimQ4)
                .numVerbatimQ5(numVerbatimQ5)
                .numExactVerbatim(numExactVerbatim)
                .statsQ1(statsQ1)
                .statsQ2(statsQ2)
                .statsQ3(statsQ3)
                .statsQ4(statsQ4)
                .statsQ5(statsQ5)
                .verbatasticUsers(verbatastic)
                .build();
    }
}
