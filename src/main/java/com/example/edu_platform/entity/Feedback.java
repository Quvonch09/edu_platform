package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Feedback extends AbsEntity {
    private String feedback;
    private Integer rating; // 5 ball
    @ManyToOne
    private User teacher;
    @ManyToOne
    private Lesson lesson;
    @ManyToOne
    private Quiz quiz;
}
