package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Integer> {

    Boolean existsByRequestingFriendIdAndRequestedFriendId(Integer requestingId, Integer requestedId);
}
