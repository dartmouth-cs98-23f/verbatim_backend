package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

    UserGroup findById(int id);
}
