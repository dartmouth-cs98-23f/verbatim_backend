package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Integer> {

    Boolean existsByRequestingFriendIdAndRequestedFriendId(Integer requestingId, Integer requestedId);

    List<UserRelationship> findByRequestedFriendIdAndActiveFalse(Integer requestedId);

    List<UserRelationship> findByRequestingFriendIdAndActiveFalse(Integer requestingId);

    UserRelationship findByRequestingFriendIdAndRequestedFriendId(Integer requestingId, Integer requestedId);

    @Query(value = "SELECT * FROM user_relationship where (requesting_user_id = ?1 OR requested_user_id = ?1) AND active = TRUE",
    nativeQuery = true)
    List<UserRelationship> findActiveFriendsByUserId(Integer userId);

    @Query(value = "SELECT CASE WHEN EXISTS " +
            "(SELECT * FROM user_relationship " +
            "WHERE (requesting_user_id = ?1 OR requested_user_id = ?1) " +
            "AND (requesting_user_id = ?2 OR requested_user_id = ?2) " +
            "AND active = TRUE) " +
            "THEN 'TRUE' ELSE 'FALSE' END",
            nativeQuery = true)
    Boolean friendshipExists(Integer user1_id, Integer user2_id);
}
