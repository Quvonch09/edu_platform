package uz.sfera.edu_platform.payload.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import uz.sfera.edu_platform.entity.enums.WeekDay;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqRoom {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Bush bulmasin")
    private String name;

    @NotBlank(message = "Bush bulmasin")
    private String color;

    @Schema(description = "HH:mm formatda vaqt", example = "09:15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "HH:mm formatda vaqt", example = "22:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
