package com.example.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonDTO {
    private Long lessonId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String videoLink;
    private List<Long> fileIds;
}
