package uz.sfera.edu_platform.payload.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqHomework {
    private String answer;
    private Long fileId;
    private Long taskId;
}
