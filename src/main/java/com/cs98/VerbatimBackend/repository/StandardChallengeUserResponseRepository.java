package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.StandardChallengeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardChallengeUserResponseRepository extends JpaRepository<StandardChallengeResponse, Integer> {

    StandardChallengeResponse findByUserIdAndChallenge(int userId, GroupChallenge challenge);
}
