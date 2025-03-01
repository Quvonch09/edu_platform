package uz.sfera.edu_platform.payload.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import uz.sfera.edu_platform.entity.enums.WeekDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqGroup {

    @NotBlank(message = "Bush bulmasin")
    private String groupName;

    @NotNull(message = "Bush bulmasin")
    private Long categoryId;

    @NotNull(message = "Bush bulmasin")
    private Long teacherId;

    @NotBlank(message = "Bush bulmasin")
    private LocalDate startDate;

    @NotNull(message = "Bush bulmasin")
    private Long roomId;

    @NotBlank(message = "Bush bulmasin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotBlank(message = "Bush bulmasin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private List<Long> dayIds;
}
