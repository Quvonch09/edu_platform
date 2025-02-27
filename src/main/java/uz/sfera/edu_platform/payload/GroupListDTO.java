package uz.sfera.edu_platform.payload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupListDTO {
    private Long id;
    private String name;
    private String teacherName;
    private String categoryName;
    private LocalDate startDate;
    private LocalDate endDate;
}
