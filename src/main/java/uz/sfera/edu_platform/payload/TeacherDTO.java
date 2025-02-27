package uz.sfera.edu_platform.payload;

import uz.sfera.edu_platform.payload.res.ResCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    @Schema(hidden = true)
    private List<ResCategory> categories;
    @Schema(hidden = true)
    private Integer groupCount;
    private Long groupId;
    private String groupName;
    private Long fileId;
    private Boolean active;
}
