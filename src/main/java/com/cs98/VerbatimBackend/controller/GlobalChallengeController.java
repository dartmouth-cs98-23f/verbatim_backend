package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.repository.CategoryRepository;
import com.cs98.VerbatimBackend.response.GetGlobalChallengeQuestionResponse;
import com.cs98.VerbatimBackend.response.GlobalChallengeUserSubmissionResponse;
import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeUserResponseRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequest;
import com.cs98.VerbatimBackend.service.GlobalChallengeService;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private GlobalChallengeService globalChallengeService;

    @GetMapping(path = "api/v1/globalChallenge")
    public ResponseEntity<GetGlobalChallengeQuestionResponse> getDailyChallenge() {
        GlobalChallenge dailyChallenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();


        GetGlobalChallengeQuestionResponse response = GetGlobalChallengeQuestionResponse.builder()
                .q1(dailyChallenge.getQ1().getContent())
                .q2(dailyChallenge.getQ2().getContent())
                .q3(dailyChallenge.getQ3().getContent())
                .categoryQ1(dailyChallenge.getQ1().getCategory().getTitle())
                .categoryQ2(dailyChallenge.getQ2().getCategory().getTitle())
                .categoryQ3(dailyChallenge.getQ3().getCategory().getTitle())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "api/v1/submitGlobalResponse")
    public ResponseEntity<GlobalChallengeUserSubmissionResponse> submitResponse(
            @RequestBody SubmitGlobalChallengeAnswerRequest request) {
        if (ObjectUtils.isEmpty(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        GlobalChallengeUserSubmissionResponse response = globalChallengeService.submitGlobalResponse(request);

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }
        return ResponseEntity.ok(response);
    }
}
