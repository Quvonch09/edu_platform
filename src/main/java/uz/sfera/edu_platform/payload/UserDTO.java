package uz.sfera.edu_platform.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @Schema(hidden = true)
    private Long id;
    private String fullName;
    private String phoneNumber;
    @Schema(hidden = true)
    private String role;
    private Long fileId;
}
