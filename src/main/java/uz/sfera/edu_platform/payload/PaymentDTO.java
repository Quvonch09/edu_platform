package uz.sfera.edu_platform.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private Long countPayment;
    private Double totalPrice;
}
