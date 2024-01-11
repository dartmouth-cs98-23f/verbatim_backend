package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRolename(String rolename);
}
