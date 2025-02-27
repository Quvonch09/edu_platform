package uz.sfera.edu_platform.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFeedback {
    private Long id;
    private String feedback;
    private String studentName;
    private LocalDateTime feedbackTime;
    private int rating;
}
