package com.valanse.recommend.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentQuizId implements Serializable {
    private Integer quizId;

    private Integer commentId;
}
