package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTask {
    @NotBlank(message = "Task title bo'sh bo'lmasin")
    private String title;
    private Long fileId;
    @NotBlank(message = "Lesson tanlanishi zarur")
    private Long lessonId;
}
