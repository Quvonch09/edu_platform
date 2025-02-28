package uz.sfera.edu_platform.payload.req;

import lombok.*;
import uz.sfera.edu_platform.entity.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqGroup {
    private String groupName;
    private Long categoryId;
    private Long teacherId;
    private LocalDate startDate;
    private Long roomId;
    private String startTime;
    private String endTime;
    private List<WeekDay> dayIds;
}
