package com.cs98.VerbatimBackend.schedule;

import com.cs98.VerbatimBackend.model.Category;
import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.Question;
import com.cs98.VerbatimBackend.repository.CategoryRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalChallengeGenerator {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Scheduled(fixedRate = 30000)
    public void createDailyChallenge() {
        List<Category> dailyCategories = categoryRepository.fetchThreeRandomCategories();
        List<Question> dailyQuestions = new ArrayList<>();
        for (Category category : dailyCategories) {
            Question question = questionRepository.fetchRandomQuestionByCategoryId(category.getId());
            dailyQuestions.add(question);
        }

        GlobalChallenge globalChallenge = GlobalChallenge.builder()
                .date(Date.valueOf(LocalDate.now()))
                .q1(dailyQuestions.get(0).getContent())
                .q2(dailyQuestions.get(1).getContent())
                .q3(dailyQuestions.get(2).getContent())
                .build();

        globalChallengeRepository.save(globalChallenge);
    }
}
