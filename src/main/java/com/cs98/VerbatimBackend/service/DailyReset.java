package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.model.Category;
import com.cs98.VerbatimBackend.model.GlobalChallenge;
import com.cs98.VerbatimBackend.model.Question;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.repository.CategoryRepository;
import com.cs98.VerbatimBackend.repository.GlobalChallengeRepository;
import com.cs98.VerbatimBackend.repository.QuestionRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DailyReset {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GlobalChallengeRepository globalChallengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "@midnight", zone = "EST") // runs every day at midnight
    public void dailyBatchReset() {

        // get all users
        List<User> all_users = userRepository.findAll();

        // create an empty list
        List<User> updated_users = new ArrayList<>();

        // loop through all users
        for (User user: all_users) {
            if (!user.getHasCompletedDailyChallenge()) {    // if the user has not completed the daily challenge
                user.setStreak(0);                          // reset streak to zero
            } else {
                user.setHasCompletedDailyChallenge(false);  // otherwise, reset the daily challenge indicator
            }
            updated_users.add(user);                        // add the user to the updated list
        }
        userRepository.saveAll(updated_users);

//        List<Category> dailyCategories = categoryRepository.fetchThreeRandomCategories();

        // get a random question from each category
//        List<Question> dailyQuestions = new ArrayList<>();
//        for (Category category : dailyCategories) {
//            Question question = questionRepository.fetchRandomQuestionByCategoryId(category.getId());
//            dailyQuestions.add(question);
//        }

        // build the row in the global challenge table
//        GlobalChallenge globalChallenge = GlobalChallenge.builder()
//                .date(Date.valueOf(LocalDate.now()))
//                .q1(dailyQuestions.get(0))
//                .q2(dailyQuestions.get(1))
//                .q3(dailyQuestions.get(2))
//                .build();

        GlobalChallenge globalChallenge = GlobalChallenge.builder()
                .date(Date.valueOf(LocalDate.now()))
                .q1(questionRepository.findById(75).orElse(null))
                .q2(questionRepository.findById(146).orElse(null))
                .q3(questionRepository.findById(346).orElse(null))
                .build();

        // save the global challenge
        globalChallengeRepository.save(globalChallenge);
    }
}
