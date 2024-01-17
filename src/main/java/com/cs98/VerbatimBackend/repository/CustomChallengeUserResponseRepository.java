package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.CustomChallengeResponse;
import com.cs98.VerbatimBackend.model.StandardChallengeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomChallengeUserResponseRepository extends JpaRepository<CustomChallengeResponse, Integer> {
}
