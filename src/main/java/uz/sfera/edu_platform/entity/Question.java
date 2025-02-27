package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.enums.QuestionEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    @Enumerated(EnumType.STRING)
    private QuestionEnum questionEnum;
    @ManyToOne
    private Quiz quiz;
}
