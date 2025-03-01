package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqLessonFiles {
    @NotBlank(message = "Lesson tanlanishi kk")
    private Long lessonId;
    @NotBlank(message = "Fayllar bo'sh bo'lishi ")
    private List<Long> fileIds;
}
