package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.Question;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeUserResponseRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequest;
import com.cs98.VerbatimBackend.response.GlobalChallengeUserSpecificResponse;
import com.cs98.VerbatimBackend.response.QuestionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class GlobalChallengeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Autowired
    private GlobalChallengeUserResponseRepository globalChallengeUserResponseRepository;

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

        int numExactVerbatim = globalChallengeUserResponseRepository.
                countByResponseQ1AndResponseQ2AndResponseQ3AndGlobalChallengeIdAndUserIdNot(
                        response.getResponseQ1(),
                        response.getResponseQ2(),
                        response.getResponseQ3(),
                        response.getGlobalChallenge().getId(),
                        user.getId()
                );

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
                .build();

        return GlobalChallengeUserSpecificResponse.builder()
                .q1(response.getGlobalChallenge().getQ1().getContent())
                .q2(response.getGlobalChallenge().getQ2().getContent())
                .q3(response.getGlobalChallenge().getQ3().getContent())
                .categoryQ1(response.getGlobalChallenge().getQ1().getCategory().getTitle())
                .categoryQ2(response.getGlobalChallenge().getQ2().getCategory().getTitle())
                .categoryQ3(response.getGlobalChallenge().getQ3().getCategory().getTitle())
                .responseQ1(response.getResponseQ1())
                .responseQ2(response.getResponseQ2())
                .responseQ3(response.getResponseQ3())
                .totalResponses(totalResponses)
                .numVerbatimQ1(numVerbatimQ1)
                .numVerbatimQ2(numVerbatimQ2)
                .numVerbatimQ3(numVerbatimQ3)
                .numExactVerbatim(numExactVerbatim)
                .statsQ1(statsQ1)
                .statsQ2(statsQ2)
                .statsQ3(statsQ3)
                .build();
    }
}
