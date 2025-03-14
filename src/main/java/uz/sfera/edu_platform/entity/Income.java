package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.entity.enums.PaymentStatusEnum;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
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
