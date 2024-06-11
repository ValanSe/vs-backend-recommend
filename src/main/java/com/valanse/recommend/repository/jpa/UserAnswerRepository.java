package com.valanse.recommend.repository.jpa;

import com.valanse.recommend.entity.Quiz;
import com.valanse.recommend.entity.UserAnswer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {

    List<Quiz> findByUserIdAndPreferenceGreaterThanEqual(Integer userId, int preference);

    @Query("SELECT ua.quizId " +
            "FROM UserAnswer ua " +
            "WHERE ua.userId " +
            "IN :similarUserIds AND ua.quizId " +
            "NOT IN " +
                "(SELECT ua2.quizId " +
                "FROM UserAnswer ua2 " +
                "WHERE ua2.userId = :userId) " +
            "ORDER BY ua.preference DESC")
    List<Integer> findRecommendedQuizzes(
            @Param("userId") Integer userId,
            @Param("similarUserIds") List<Integer> similarUserIds,
            Pageable pageable);

    @Query("SELECT ua.quizId " +
            "FROM UserAnswer ua " +
            "WHERE ua.userId " +
            "IN :otherUserIds AND ua.quizId " +
            "NOT IN " +
            "(SELECT ua2.quizId " +
            "FROM UserAnswer ua2 " +
            "WHERE ua2.userId = :userId) " +
            "ORDER BY ua.preference DESC")
    List<Integer> findRecommendedQuizzesFromOtherUsers(
            @Param("userId") Integer userId,
            @Param("otherUserIds") List<Integer> otherUserIds,
            Pageable pageable);
}