package com.cs98.VerbatimBackend.repository;

import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalChallengeUserResponseRepository extends JpaRepository<GlobalChallengeUserResponse, Integer> {

    GlobalChallengeUserResponse findByUserIdAndGlobalChallengeId(int userId, int globalChallengeId);

    List<GlobalChallengeUserResponse> findByGlobalChallengeIdAndUserIdIn(int globalChallengeId, List<Integer> userIdList);
    Integer countByGlobalChallengeId(int id);

    Integer countByResponseQ1AndGlobalChallengeIdAndUserIdNot(String responseQ1, int challengeId, int userId);

    Integer countByResponseQ2AndGlobalChallengeIdAndUserIdNot(String responseQ2, int challengeId, int userId);

    Integer countByResponseQ3AndGlobalChallengeIdAndUserIdNot(String responseQ3, int challengeId, int userId);

    Integer countByResponseQ4AndGlobalChallengeIdAndUserIdNot(String responseQ4, int challengeId, int userId);

    Integer countByResponseQ5AndGlobalChallengeIdAndUserIdNot(String responseQ5, int challengeId, int userId);

    Integer countByResponseQ1AndGlobalChallengeId(String responseQ1, int challengeId);

    Integer countByResponseQ2AndGlobalChallengeId(String responseQ1, int challengeId);

    Integer countByResponseQ3AndGlobalChallengeId(String responseQ1, int challengeId);

    Integer countByResponseQ4AndGlobalChallengeId(String responseQ4, int challengeId);

    Integer countByResponseQ5AndGlobalChallengeId(String responseQ5, int challengeId);

    Integer countByResponseQ1AndResponseQ2AndResponseQ3AndResponseQ4AndResponseQ5AndGlobalChallengeIdAndUserIdNot(
            String responseQ1,
            String responseQ2,
            String responseQ3,
            String responseQ4,
            String responseQ5,
            int challengeId,
            int userId);

    Boolean existsByUserIdAndGlobalChallengeId(int userId, int challengeId);

    // select mode() within group (order by responseq1) from global_challenge_response where global_challenge_id = 1;
    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq1) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1",
            nativeQuery = true)
    String findMostPopularQ1ResponseByChallengeId(Integer id);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq1) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq1 != ?2",
            nativeQuery = true)
    String findSecondMostPopularQ1ResponseByChallengeId(Integer id, String mostPopularQ1);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq1) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq1 != ?2 AND responseq1 != ?3",
            nativeQuery = true)
    String findThirdMostPopularQ1ResponseByChallengeId(
            Integer id,
            String mostPopularQ1,
            String secondMostPopularQ1);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq2) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 ",
            nativeQuery = true)
    String findMostPopularQ2ResponseByChallengeId(Integer id);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq2) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq2 != ?2",
            nativeQuery = true)
    String findSecondMostPopularQ2ResponseByChallengeId(Integer id, String mostPopularQ2);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq2) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq2 != ?2 AND responseq2 != ?3",
            nativeQuery = true)
    String findThirdMostPopularQ2ResponseByChallengeId(
            Integer id,
            String mostPopularQ2,
            String secondMostPopularQ2);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq3) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1",
            nativeQuery = true)
    String findMostPopularQ3ResponseByChallengeId(Integer id);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq3) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq3 != ?2",
            nativeQuery = true)
    String findSecondMostPopularQ3ResponseByChallengeId(Integer id, String mostPopularQ3);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq3) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq3 != ?2 AND responseq3 != ?3",
            nativeQuery = true)
    String findThirdMostPopularQ3ResponseByChallengeId(
            Integer id,
            String mostPopularQ3,
            String secondMostPopularQ3);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq4) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1",
            nativeQuery = true)
    String findMostPopularQ4ResponseByChallengeId(Integer id);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq4) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq4 != ?2",
            nativeQuery = true)
    String findSecondMostPopularQ4ResponseByChallengeId(Integer id, String mostPopularQ4);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq4) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq4 != ?2 AND responseq4 != ?3",
            nativeQuery = true)
    String findThirdMostPopularQ4ResponseByChallengeId(
            Integer id,
            String mostPopularQ3,
            String secondMostPopularQ4);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq5) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1",
            nativeQuery = true)
    String findMostPopularQ5ResponseByChallengeId(Integer id);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq5) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq5 != ?2",
            nativeQuery = true)
    String findSecondMostPopularQ5ResponseByChallengeId(Integer id, String mostPopularQ5);

    @Query(value = "SELECT MODE() WITHIN GROUP (ORDER BY responseq5) " +
            "FROM global_challenge_response WHERE global_challenge_id = ?1 " +
            "AND responseq5 != ?2 AND responseq5 != ?3",
            nativeQuery = true)
    String findThirdMostPopularQ5ResponseByChallengeId(
            Integer id,
            String mostPopularQ5,
            String secondMostPopularQ5);

    List<GlobalChallengeUserResponse> findAllByGlobalChallengeIdAndResponseQ1(int globalChallengeId, String responseQ1);

    List<GlobalChallengeUserResponse> findAllByGlobalChallengeIdAndResponseQ2(int globalChallengeId, String responseQ2);

    List<GlobalChallengeUserResponse> findAllByGlobalChallengeIdAndResponseQ3(int globalChallengeId, String responseQ3);

    List<GlobalChallengeUserResponse> findAllByGlobalChallengeIdAndResponseQ4(int globalChallengeId, String responseQ4);

    List<GlobalChallengeUserResponse> findAllByGlobalChallengeIdAndResponseQ5(int globalChallengeId, String responseQ5);
}
