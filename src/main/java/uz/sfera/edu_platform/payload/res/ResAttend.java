package uz.sfera.edu_platform.payload.res;

import uz.sfera.edu_platform.payload.AttendDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResAttend {
    private Long studentId;
    private String fullName;
    private List<AttendDto> attendList;
}
