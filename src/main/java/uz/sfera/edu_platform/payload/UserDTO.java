package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import uz.sfera.edu_platform.payload.res.ResCategory;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Bush bulmasin")
    private String fullName;

    @NotBlank(message = "Bush bulmasin")
    private String phoneNumber;

    @Schema(hidden = true)
    private String role;

    private Long fileId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
