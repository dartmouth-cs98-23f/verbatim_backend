package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalChallengeRepository extends JpaRepository<GlobalChallenge, Integer> {

    GlobalChallenge findFirst1ByOrderByDateDesc();
}
