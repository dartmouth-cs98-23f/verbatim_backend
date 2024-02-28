package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "SELECT * FROM question WHERE category_id = ?1 ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question fetchRandomQuestionByCategoryId(Integer categoryId);

    @Query(value = "SELECT * FROM question " +
            "WHERE category_id = ?1 " +
            "AND last_global_chal_used < ?2 " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question fetchRandomQuestionByCategoryIdBeforeChallenge(Integer categoryId, Integer challengeId);

}
