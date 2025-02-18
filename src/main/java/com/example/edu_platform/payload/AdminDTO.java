package com.example.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO {
    @Schema(hidden = true)
    private Long id;
    private String fullName;
    private String phoneNumber;
    private Long fileId;
}
