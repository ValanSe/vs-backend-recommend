package com.valanse.recommend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CommentQuizId.class)
public class CommentQuiz {
    @Id
    private Integer quizId;

    @Id
    private Integer commentId;
}
