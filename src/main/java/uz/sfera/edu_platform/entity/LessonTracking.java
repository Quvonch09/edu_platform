package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.sfera.edu_platform.entity.template.AbsEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LessonTracking extends AbsEntity {
    @ManyToOne
    private Lesson lesson;
    @ManyToOne
    private Group group;
}
