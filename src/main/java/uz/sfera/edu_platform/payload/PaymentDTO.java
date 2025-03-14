package uz.sfera.edu_platform.payload;

import uz.sfera.edu_platform.entity.enums.OutcomeStatus;
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
    private OutcomeStatus paymentStatus;
    private LocalDate paymentDate;
    private Double price;
    private boolean paid;
}
