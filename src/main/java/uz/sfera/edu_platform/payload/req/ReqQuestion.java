package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqQuestion {
    @NotBlank(message = "Savol matni bo'sh bo'lmasin")
    private String questionText;
    @NotBlank(message = "Quiz tanlanishi majburiy")
    private Long quizId;
    @NotBlank(message = "Javoblar qo'shish majburiy")
    private List<ReqOption> reqOptionList;
}
