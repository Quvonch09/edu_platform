package uz.sfera.edu_platform.payload;

import lombok.*;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeDTO {
    private Long id;
    private String studentName;
    private LocalDate paymentDate;
    private Month paymentMonth;
    private Double price;
    private boolean paid;
}
