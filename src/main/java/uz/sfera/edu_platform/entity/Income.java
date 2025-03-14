package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import uz.sfera.edu_platform.entity.enums.OutcomeStatus;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Income extends AbsEntity {

    private Double price;

    @ManyToOne
    private User student;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private Month month;

    private boolean paid;
}
