package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String category;
    private boolean deleted;
}
