package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.sfera.edu_platform.entity.template.AbsEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result extends AbsEntity {

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private int totalQuestion;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Quiz quiz;
}
