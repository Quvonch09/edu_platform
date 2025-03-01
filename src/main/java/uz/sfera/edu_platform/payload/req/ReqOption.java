package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqOption {
    @NotBlank(message = "Javob matni bo'sh bo'lishi mumkin emas!")
    private String text;

    @NotBlank(message = "Javobni to'g'ri yoki noto'g'ri ekanligini kiritish zarur")
    private boolean isCorrect;
}
