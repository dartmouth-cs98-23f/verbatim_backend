package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.Category;
import com.cs98.VerbatimBackend.model.CustomQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomQuestionRepository extends JpaRepository<CustomQuestion, Integer> {
}
