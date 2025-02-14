package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LessonTracking extends AbsEntity {
    @OneToOne
    private Lesson lesson;
    @OneToOne
    private Group group;
}
