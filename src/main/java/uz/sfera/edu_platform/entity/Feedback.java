package uz.sfera.edu_platform.entity;

import jakarta.persistence.Column;
import uz.sfera.edu_platform.entity.template.AbsEntity;
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

    @Column(nullable = false)
    private Integer rating; // 5 ball --- byte

    @ManyToOne
    private User teacher;

    @ManyToOne
    private Lesson lesson;

    @ManyToOne
    private Quiz quiz;

    @ManyToOne
    private User student;
}
