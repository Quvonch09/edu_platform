package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqQuestion {

    @NotBlank(message = "Savol matni bo'sh bo'lmasin")
    private String questionText;

    @NotNull(message = "Quiz tanlanishi majburiy")
    private Long quizId;

    @NotEmpty(message = "Javoblar qo'shish majburiy")
    private List<ReqOption> reqOptionList;
}
