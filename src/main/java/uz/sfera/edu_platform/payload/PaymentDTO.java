package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import uz.sfera.edu_platform.entity.enums.PaymentStatusEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private String fullName;
    private PaymentStatusEnum paymentStatus;
    private LocalDate paymentDate;
    private Double price;
    private boolean paid;
}
