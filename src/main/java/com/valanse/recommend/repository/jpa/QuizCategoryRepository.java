package com.valanse.recommend.repository.jpa;

import com.valanse.recommend.entity.QuizCategory;
import com.valanse.recommend.entity.QuizCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizCategoryRepository extends JpaRepository<QuizCategory, QuizCategoryId> {

    void deleteByQuizId(Integer quizId);

    List<QuizCategory> findByCategory(String category);

    List<QuizCategory> findByCategoryContaining(String keyword);
}