package uz.sfera.edu_platform.payload.res;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResTeacherStatistic {

    private Integer studentCount;
    private Integer groupCount;
    private Integer paidAllCount;
    private Integer paidCount;
}
