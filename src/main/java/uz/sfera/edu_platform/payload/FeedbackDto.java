package uz.sfera.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedbackDto {

    private String feedback;
    private int rating;
    private long quizId;
    private long lessonId;
}
