package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLessonTracking {
    @NotNull(message = "Lesson tanlanishi zarur")
    private Long lessonId;

    @NotNull(message = "Guruh tanlanishi zarur")
    private Long groupId;
}
