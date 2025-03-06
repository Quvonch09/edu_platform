package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeworkDTO {
    private Long homeworkId;
    private String answer;
    private int ball;
    private String studentName;
    private Long taskId;
    private Long fileId;
}
