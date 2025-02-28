package uz.sfera.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private byte duration;
    private double price;
    @Schema(hidden = true)
    private boolean active;
    private Long fileId;
}
