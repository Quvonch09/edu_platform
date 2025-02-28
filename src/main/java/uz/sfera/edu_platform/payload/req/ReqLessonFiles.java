package uz.sfera.edu_platform.payload.req;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqLessonFiles {
    private Long lessonId;
    private List<Long> fileIds;
}
