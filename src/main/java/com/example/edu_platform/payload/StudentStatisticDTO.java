package com.example.edu_platform.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentStatisticDTO {
    private Long id;
    private String name;
    private Double ball;
    private Double rating;
}
