package uz.sfera.edu_platform.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResFeedback {
    private String teacherName;
    private Long teacherId;
    private Integer lessonBall;
    private Integer countLesson;
    private Integer quizBall;
    private Integer quizCount;
    private Integer teacherBall;
    private Integer teacherCount;
}
