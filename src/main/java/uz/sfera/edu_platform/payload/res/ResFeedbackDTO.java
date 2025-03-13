package uz.sfera.edu_platform.payload.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public interface ResFeedbackDTO {
    Long getId();
    String getFeedback();
    String getStudentName();
    LocalDateTime getFeedbackTime();
    int getRating();
    @Schema(hidden = true)
    Long getQuizId();
    @Schema(hidden = true)
    Long getLessonId();
}
