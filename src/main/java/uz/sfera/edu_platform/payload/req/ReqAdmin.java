package uz.sfera.edu_platform.payload.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqAdmin {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Bush bulmasin")
    private String fullName;

    @NotBlank(message = "Bush bulmasin")
    @Pattern(regexp = "^998([0-9][012345789]|[0-9][125679]|7[01234569])[0-9]{7}$",
            message = "Invalid Uzbekistan phone number")
    private String phoneNumber;

    @NotBlank(message = "Bush bulmasin")
    private String password;
    private Long fileId;
}
