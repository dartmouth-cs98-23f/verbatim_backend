package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalChallengeUserResponseRepository extends JpaRepository<GlobalChallengeUserResponse, Integer> {

}
