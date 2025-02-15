package com.example.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private Double price;
    @Schema(hidden = true)
    private Boolean active;
    private Long fileId;
}
