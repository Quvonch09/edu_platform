package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqQuiz {
    @NotBlank(message = "Quiz nomi bo'sh bo'lmasin")
    private String title;
    @NotBlank(message = "Kategoriya bo'sh bo'lishi mumkin emas")
    private Long lessonId;
    private Integer questionCount;
    private Integer duration;
}
