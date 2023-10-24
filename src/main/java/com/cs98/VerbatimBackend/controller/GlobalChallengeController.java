package com.cs98.VerbatimBackend.controller;

import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalChallengeController {

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @GetMapping(path = "api/v1/globalChallenge")
    public ResponseEntity<GlobalChallenge> getDailyChallenge() {
        GlobalChallenge dailyChallenge = globalChallengeRepository.findFirst1ByOrderByDateDesc();
        return ResponseEntity.ok(dailyChallenge);
    }
}
