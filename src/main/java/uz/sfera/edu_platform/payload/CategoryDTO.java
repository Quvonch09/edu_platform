package uz.sfera.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(hidden = true)
    private Long id;
    @NotBlank(message = "Nom bo'sh bo'lmasligi kerak")
    private String name;
    private String description;
    private byte duration;
    @NotBlank(message = "Narx bo'sh bo'lmasligi kerak")
    private double price;
    @Schema(hidden = true)
    private boolean active;
    private Long fileId;
}
