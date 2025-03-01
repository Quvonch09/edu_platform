package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExamResultRequest {

    @NotNull(message = "Student id kiritilishi zarur")
    private Long studentId;

    private byte ball;
}
