package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.StandardChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardChallengeRepository extends JpaRepository<StandardChallenge, Integer> {

}
