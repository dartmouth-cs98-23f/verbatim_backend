package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroupJunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupJunctionRepository extends JpaRepository<UserGroupJunction, Integer> {

    List<UserGroupJunction> findByUserId(Integer userId);

    List<UserGroupJunction> findByGroupId(Integer groupId);
    Boolean existsByUserIdAndGroupId(Integer userId, Integer groupId);

    UserGroupJunction findByUserIdAndGroupId(Integer userId, Integer groupId);
}
