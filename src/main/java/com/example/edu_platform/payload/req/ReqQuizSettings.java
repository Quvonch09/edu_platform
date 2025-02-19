package com.example.edu_platform.payload.req;

import lombok.*;

@Getter
@Setter
public class ReqQuizSettings {
    private Long quizId;
    private Integer questionCount;
    private Integer duration;
}
