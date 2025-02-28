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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment extends AbsEntity {
    private Double price;
    @ManyToOne
    private User student;
    private LocalDate paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentEnum paymentType;
}
