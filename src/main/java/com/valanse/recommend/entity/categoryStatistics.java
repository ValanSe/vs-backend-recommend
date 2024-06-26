package com.valanse.recommend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class categoryStatistics {

    @Id
    private String category;

    private Integer totalAnswers;
    private Integer totalScore;

    @Formula("total_score / total_answers")
    private Float avgPreference;


}
