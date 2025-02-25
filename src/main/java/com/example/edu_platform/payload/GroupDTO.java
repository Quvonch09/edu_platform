package com.example.edu_platform.payload;

import com.example.edu_platform.entity.enums.WeekDay;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    private Long teacherId;
    private String categoryName;
    private Long categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer countEndMonth;
    private Integer countAllLessons;
    private Integer countGroupLessons;
    private Boolean active;
    private String roomName;
    private Long roomId;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> weekDays;

}
