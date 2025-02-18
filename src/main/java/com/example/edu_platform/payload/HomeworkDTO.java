package com.example.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeworkDTO {
    private Long homeworkId;
    private String answer;
    private Integer ball;
    private String studentName;
    private Long taskId;
}
