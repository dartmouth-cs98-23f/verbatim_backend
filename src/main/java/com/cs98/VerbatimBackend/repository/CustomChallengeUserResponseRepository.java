package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.CustomChallengeResponse;
import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.StandardChallengeResponse;
import com.cs98.VerbatimBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChallengeUserResponseRepository extends JpaRepository<CustomChallengeResponse, Integer> {
    public List<CustomChallengeResponse> findAllByGroupChallenge(GroupChallenge groupChallenge);

    Boolean existsByUserIdAndChallengeId(int userId, int challengeId);
}
