package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqPayment {

    private String userName;

    @NotNull(message = "Narx bo‘sh bo‘lishi mumkin emas")
    private Double price;

    @NotNull(message = "Payment date bush bulmasin")
    private LocalDate paymentDate;

    private boolean paid;
}
