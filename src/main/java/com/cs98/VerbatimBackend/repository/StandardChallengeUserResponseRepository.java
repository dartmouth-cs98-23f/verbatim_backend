package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.StandardChallenge;
import com.cs98.VerbatimBackend.model.StandardChallengeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardChallengeUserResponseRepository extends JpaRepository<StandardChallengeResponse, Integer> {

    Boolean existsByUserIdAndChallengeId(int userId, int challengeId);

    List<StandardChallengeResponse> findAllByChallenge(StandardChallenge challenge);
}
