package uz.sfera.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDto {

    @Schema(hidden = true)
    private Long id;
    @Schema(hidden = true)
    private String fullName;
    private Long studentId;
    private boolean attendance;
    private LocalDate date;
}
