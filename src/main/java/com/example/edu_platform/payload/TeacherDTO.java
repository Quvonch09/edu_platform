package com.example.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    @Schema(hidden = true)
    private List<Long> categoryId;
    @Schema(hidden = true)
    private Integer groupCount;
    private Long fileId;
    private Boolean active;
}
