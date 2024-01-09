package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChallengeRepository extends JpaRepository<GroupChallenge, Integer> {
    GroupChallenge findByGroupAndCreatedBy(UserGroup group, User createdBy);
}
