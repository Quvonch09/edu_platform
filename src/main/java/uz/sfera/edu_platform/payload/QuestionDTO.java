package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDTO {
    private Long id;
    private String text;
    private Long fileId;
    private String difficulty;
    private Long quizId;
    private List<OptionDTO> options;
}
