package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LessonRequest {
    @NotBlank(message = "Dars nomi bo'sh bo'lishi mumkin emas")
    private String name;
    @NotBlank(message = "Tavsif bo‘sh bo‘lishi mumkin emas")
    private String description;
    @NotBlank(message = "Dars qo'shish uchun modul tanlash zarur")
    private Long moduleId;
    private String videoLink;
}
