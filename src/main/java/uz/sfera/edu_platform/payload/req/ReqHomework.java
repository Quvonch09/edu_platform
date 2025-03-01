package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqHomework {
    @NotBlank(message = "Homework matni bo'sh bo'lmasin")
    private String answer;
    private Long fileId;
    @NotBlank(message = "Homework bajarish uchun task kiritish zarur")
    private Long taskId;
}
