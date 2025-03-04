package uz.sfera.edu_platform.payload;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StartTestDTO {
    private List<QuestionDTO> questions;
    private Integer duration;
}
