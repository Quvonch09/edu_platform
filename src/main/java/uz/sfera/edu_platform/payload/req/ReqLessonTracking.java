package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLessonTracking {
    @NotBlank(message = "Lesson tanlanishi zarur")
    private Long lessonId;
    @NotBlank(message = "Guruh tanlanishi zarur")
    private Long groupId;
}
