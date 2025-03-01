package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleRequest {
    @NotBlank(message = "Modul nomi bo'sh bo'lishi mumkin emas")
    private String name;
    @NotBlank(message = "modul qoshish uchun kategoriya tanlanishi kk")
    private Long categoryId;
}
