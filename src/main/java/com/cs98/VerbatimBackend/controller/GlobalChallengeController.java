package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.GetGlobalChallengeQuestionResponse;
import com.cs98.VerbatimBackend.GlobalChallengeUserSubmissionResponse;
import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeUserResponseRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalChallengeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Autowired
    private GlobalChallengeUserResponseRepository globalChallengeUserResponseRepository;

    @GetMapping(path = "api/v1/globalChallenge")
    public ResponseEntity<GetGlobalChallengeQuestionResponse> getDailyChallenge() {
        GlobalChallenge dailyChallenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();

        GetGlobalChallengeQuestionResponse response = GetGlobalChallengeQuestionResponse.builder()
                .q1(dailyChallenge.getQ1().getContent())
                .q2(dailyChallenge.getQ2().getContent())
                .q3(dailyChallenge.getQ3().getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "api/v1/submitGlobalResponse")
    public ResponseEntity<GlobalChallengeUserSubmissionResponse> submitResponse(
            @RequestBody SubmitGlobalChallengeAnswerRequest request) {
        User respondingUser = userRepository.findByUsername(request.getUsername());
        if (ObjectUtils.isEmpty(respondingUser)) {
            return ResponseEntity.badRequest().build();
        }
        GlobalChallenge challenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();

        GlobalChallengeUserResponse submission = GlobalChallengeUserResponse.builder()
                .user(respondingUser)
                .globalChallenge(challenge)
                .responseQ1(request.getResponseQ1())
                .responseQ2(request.getResponseQ2())
                .responseQ3(request.getResponseQ3())
                .build();


        globalChallengeUserResponseRepository.save(submission);

        GlobalChallengeUserSubmissionResponse httpResponse = GlobalChallengeUserSubmissionResponse.builder()
                .responseQ1(submission.getResponseQ1())
                .responseQ2(submission.getResponseQ2())
                .responseQ3(submission.getResponseQ3())
                .build();

        return ResponseEntity.ok(httpResponse);
    }
}
