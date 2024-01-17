package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.UserGroupJunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupJunctionRepository extends JpaRepository<UserGroupJunction, Integer> {

}
