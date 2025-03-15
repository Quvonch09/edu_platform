package uz.sfera.edu_platform.payload.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqOutcome {

    @Schema(hidden = true)
    private Long id;

    private String teacherName;

    private double price;

    @NotNull(message = "Payment date bush bulmasin")
    private LocalDate paymentDate;

    @Schema(hidden = true)
    private String outcomeStatus;

    @Schema(hidden = true)
    private String month;
}
