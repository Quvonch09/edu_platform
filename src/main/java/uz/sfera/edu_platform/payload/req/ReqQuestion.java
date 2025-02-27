package uz.sfera.edu_platform.payload.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqQuestion {
    private String questionText;
    private Long quizId;
}
