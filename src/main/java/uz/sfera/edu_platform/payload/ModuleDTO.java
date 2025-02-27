package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleDTO {
    private Long id;
    private String name;
    @Schema(hidden = true)
    private String category;
}
