package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT * FROM category ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<Category> fetchThreeRandomCategories();
}
