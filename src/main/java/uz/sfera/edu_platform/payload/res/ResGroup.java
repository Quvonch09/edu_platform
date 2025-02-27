package uz.sfera.edu_platform.payload.res;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResGroup {
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
    private List<String> days;
}
