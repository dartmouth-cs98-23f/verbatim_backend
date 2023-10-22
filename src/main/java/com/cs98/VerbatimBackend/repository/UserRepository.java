package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByUsername(String username);

    User findByUsername(String username);

    Boolean existsByEmail(String email);

    User findByEmail(String email);
}
