package com.valanse.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QuizCategoryId implements Serializable {
    private String category;
    private Integer quizId;
}
