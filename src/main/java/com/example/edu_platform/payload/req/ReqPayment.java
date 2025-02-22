package com.example.edu_platform.payload.req;

import com.example.edu_platform.entity.enums.PaymentEnum;
import com.example.edu_platform.entity.enums.PaymentStatusEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqPayment {
    private Long userId;
    private Double price;
    private LocalDate paymentDate;
}
