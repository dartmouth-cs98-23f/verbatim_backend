package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroup;
import com.cs98.VerbatimBackend.model.UserGroupJunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupJunctionRepository extends JpaRepository<UserGroupJunction, Integer> {
    public boolean existsByGroupAndUser(UserGroup group, User user);

    List<UserGroupJunction> findByUserId(Integer userId);

    List<UserGroupJunction> findByGroupId(Integer groupId);
    Boolean existsByUserIdAndGroupId(Integer userId, Integer groupId);

    UserGroupJunction findByUserIdAndGroupId(Integer userId, Integer groupId);

    @Query(value = "SELECT group_id FROM user_group_junction " +
            "WHERE user_id IN (?1, ?2) " +
            "GROUP BY group_id " +
            "HAVING COUNT(DISTINCT user_id) = 2 " +
            "AND COUNT(*) = 2 " +
            "AND NOT EXISTS (SELECT 1 " +
            "FROM user_group_junction as t2 " +
            "WHERE t2.group_id = user_group_junction.group_id " +
            "AND t2.user_id NOT IN (?1, ?2))",
            nativeQuery = true)
    int getGroupIdForFriendGroup(int user1Id, int user2Id);

    @Query(value = "SELECT * FROM user_group_junction " +
            "WHERE user_id = ?1 " +
            "AND group_id NOT IN (" +
            "SELECT group_id FROM user_group_junction " +
            "GROUP BY group_id " +
            "HAVING COUNT(*) = 2 )",
            nativeQuery = true)
    List<UserGroupJunction> findMultiGroupsByUserId(Integer userId);

    @Query(value = "SELECT * FROM user_group_junction " +
            "WHERE user_id = ?1 " +
            "AND group_id NOT IN (" +
            "SELECT group_id FROM user_group_junction " +
            "GROUP BY group_id " +
            "HAVING COUNT(*) > 2 )",
            nativeQuery = true)
    List<UserGroupJunction> findFriendGroupsByUserId(Integer userId);
}
