package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.sfera.edu_platform.entity.enums.OutcomeStatus;
import uz.sfera.edu_platform.entity.template.AbsEntity;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Outcome extends AbsEntity {

    @Column(nullable = false)
    private String teacherName;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private Month month;

    @Enumerated(EnumType.STRING)
    private OutcomeStatus outcomeStatus;
}
