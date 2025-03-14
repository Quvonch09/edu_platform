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
public class Payment extends AbsEntity {

    private Double price;

    @OneToOne
    private User student;

    private String userName;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private Month month;

    @Enumerated(EnumType.STRING)
    private OutcomeStatus paymentStatus;
//
//    @Enumerated(EnumType.STRING)
//    private PaymentEnum paymentType;

    private byte paid;
}
