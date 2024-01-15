package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.CustomChallenge;
import com.cs98.VerbatimBackend.model.CustomQuestion;
import com.cs98.VerbatimBackend.model.GroupChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChallengeRepository extends JpaRepository<CustomChallenge, Integer> {
    public List<CustomChallenge> findAllByChallenge(GroupChallenge challenge);
}
