package com.example.edu_platform.payload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDTO {
    private Long id;
    private String name;
    private Integer studentCount;
    private Integer departureStudentCount;
    private String teacherName;
    private Long categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

}
