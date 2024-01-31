package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.misc.UserDetailsPrincipal;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequestWithSignIn;
import com.cs98.VerbatimBackend.response.GetGlobalChallengeQuestionResponse;
import com.cs98.VerbatimBackend.response.GlobalChallengeQuestions;
import com.cs98.VerbatimBackend.response.GlobalChallengeUserSpecificResponse;
import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.request.SubmitGlobalChallengeAnswerRequest;
import com.cs98.VerbatimBackend.service.GlobalChallengeService;
import com.cs98.VerbatimBackend.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.instrument.classloading.glassfish.GlassFishLoadTimeWeaver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class GlobalChallengeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Autowired GlobalChallengeUserResponseRepository globalChallengeUserResponseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GlobalChallengeService globalChallengeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @PostMapping(path = "api/v1/globalChallenge")
    public ResponseEntity<GlobalChallengeUserSpecificResponse> loadDailyChallenge(@NotNull @RequestBody String username) {

        User user = userRepository.findByUsername(username);
        GlobalChallenge dailyChallenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();
        GlobalChallengeUserSpecificResponse response;
        if (user.getHasCompletedDailyChallenge()) {
            GlobalChallengeUserResponse responseForUser = globalChallengeUserResponseRepository.findByUserIdAndGlobalChallengeId(user.getId(), dailyChallenge.getId());
            response = globalChallengeService.loadGlobalChallengeStatsForUser(user, responseForUser);

        }
        else {
            response = GlobalChallengeUserSpecificResponse.builder()
                    .q1(dailyChallenge.getQ1().getContent())
                    .q2(dailyChallenge.getQ2().getContent())
                    .q3(dailyChallenge.getQ3().getContent())
                    .globalChallengeId(dailyChallenge.getId())
                    .totalResponses(globalChallengeUserResponseRepository.countByGlobalChallengeId(dailyChallenge.getId()))
                    .categoryQ1(dailyChallenge.getQ1().getCategory().getTitle())
                    .categoryQ2(dailyChallenge.getQ2().getCategory().getTitle())
                    .categoryQ3(dailyChallenge.getQ3().getCategory().getTitle())
                    .build();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "api/v1/submitGlobalResponse")
    public ResponseEntity<GlobalChallengeUserSpecificResponse> submitResponse(
            @NotNull @RequestBody SubmitGlobalChallengeAnswerRequest request) {
        if (ObjectUtils.isEmpty(request.getUsername())) {
            return ResponseEntity.status(Status.UNAUTHORIZED).build();
        }
        GlobalChallengeUserSpecificResponse response = globalChallengeService.submitGlobalResponse(request);

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "api/v1/globalChallengeNoSignIn")
    public ResponseEntity<GlobalChallengeQuestions> getGlobalQuestions() {
        GlobalChallenge globalChallenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();
        GlobalChallengeQuestions globalChallengeQuestions = GlobalChallengeQuestions.builder()
                .globalChallengeId(globalChallenge.getId())
                .q1(globalChallenge.getQ1().getContent())
                .q2(globalChallenge.getQ2().getContent())
                .q3(globalChallenge.getQ3().getContent())
                .categoryQ1(globalChallenge.getQ1().getCategory().getTitle())
                .categoryQ2(globalChallenge.getQ2().getCategory().getTitle())
                .categoryQ3(globalChallenge.getQ3().getCategory().getTitle())
                .build();

        return ResponseEntity.ok(globalChallengeQuestions);

    }

    @PostMapping(path = "api/v1/submitResponseAfterLoginOrRegister")
    public ResponseEntity<GlobalChallengeUserSpecificResponse> submitResponseAfterLoginOrRegister(@RequestBody SubmitGlobalChallengeAnswerRequestWithSignIn request) {
        User userEntityToReturn;
        if (request.getIsLogin()) {
            UserDetailsPrincipal userToAuthenticate = userService.loadUserByUsername(request.getEmailOrUsername());
            if (passwordEncoder.matches(request.getPassword(), userToAuthenticate.getPassword())) {
                userEntityToReturn = userToAuthenticate.getUser();
            } else {
                return ResponseEntity.status(Status.WRONG_PASSWORD).build();
            }
        }
        else {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(Status.EMAIL_TAKEN).build();
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(Status.USERNAME_TAKEN).build();
            }

            userEntityToReturn = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .numGlobalChallengesCompleted(0)
                    .numCustomChallengesCompleted(0)
                    .role(roleRepository.findByRolename("ROLE_USER"))
                    .streak(0)
                    .hasCompletedDailyChallenge(false)
                    .build();

            userRepository.save(userEntityToReturn);
        }

        if (request.getWithReferral()) {
            User referringUser = userRepository.findByUsername(request.getReferringUsername());

            if (ObjectUtils.isEmpty(referringUser) || ObjectUtils.isEmpty(userEntityToReturn)) {
                return ResponseEntity.status(Status.USER_NOT_FOUND).build();
            }

            UserRelationship newRel = UserRelationship.builder()
                    .requestingFriend(referringUser)
                    .requestedFriend(userEntityToReturn)
                    .active(true)
                    .build();

            userRelationshipRepository.save(newRel);
        }
        SubmitGlobalChallengeAnswerRequest submitAnswerRequest = new SubmitGlobalChallengeAnswerRequest();
        submitAnswerRequest.setUsername(request.getUsername());
        submitAnswerRequest.setResponseQ1(request.getResponseQ1());
        submitAnswerRequest.setResponseQ2(request.getResponseQ2());
        submitAnswerRequest.setResponseQ3(request.getResponseQ3());

        GlobalChallengeUserSpecificResponse response = globalChallengeService.submitGlobalResponse(submitAnswerRequest);

        if (ObjectUtils.isEmpty(response)) {
            throw new RuntimeException("failed to build response");
        }
        return ResponseEntity.ok(response);

    }

}
