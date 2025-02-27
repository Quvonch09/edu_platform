package uz.sfera.edu_platform.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendDto {
    private Long id;
    private Boolean attendance;
    private LocalDate date;
}
