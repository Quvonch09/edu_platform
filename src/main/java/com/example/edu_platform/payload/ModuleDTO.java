package com.example.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private LocalDateTime createdAt;
    private Long createdBy;
    @Schema(hidden = true)
    private LocalDateTime updatedAt;
}
