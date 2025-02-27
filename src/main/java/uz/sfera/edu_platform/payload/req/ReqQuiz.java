package uz.sfera.edu_platform.payload.req;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqQuiz {
    private String title;
    private Long lessonId;
    private Integer questionCount;
    private Integer duration;
}
