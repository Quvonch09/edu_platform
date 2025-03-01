package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.Month;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Month month;

    @Column(nullable = false)
    private byte ball;
}
