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
    private String categoryName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer countEndMonth;
    private Integer countAllLessons;
    private Integer countGroupLessons;
    private Boolean active;

}
