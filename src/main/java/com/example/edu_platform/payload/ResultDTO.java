package com.example.edu_platform.payload;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResultDTO {
    private Long id;
    private Long userId;
    private Long quizId;
    private int totalQuestion;
    private int correctAnswers;
    private Long timeTaken;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
