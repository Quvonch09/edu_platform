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

    @NotBlank(message = "Bush bulmasin")
    private String name;

    @NotBlank(message = "Bush bulmasin")
    private String description;

    private byte duration;

    private double price;
    @Schema(hidden = true)
    private Boolean active;
    private Long fileId;
}
