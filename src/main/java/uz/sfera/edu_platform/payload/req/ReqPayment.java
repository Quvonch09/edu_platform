package uz.sfera.edu_platform.payload.req;

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
