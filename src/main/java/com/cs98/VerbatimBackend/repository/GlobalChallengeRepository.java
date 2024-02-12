package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GlobalChallengeRepository extends JpaRepository<GlobalChallenge, Integer> {

    GlobalChallenge findFirst1ByOrderByDateDesc();

    @Query(value = "SELECT COUNT(*) FROM global_challenge",
                nativeQuery = true)
    Integer getGlobalChallengeDisplayNumber();


}
