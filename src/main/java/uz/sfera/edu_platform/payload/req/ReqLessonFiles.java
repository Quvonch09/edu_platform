package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqLessonFiles {

    @NotNull(message = "Lesson tanlanishi kk")
    private Long lessonId;

    private List<Long> fileIds;
}
