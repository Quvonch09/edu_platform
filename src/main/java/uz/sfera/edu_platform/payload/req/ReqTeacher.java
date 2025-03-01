package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqTeacher {

    private String fullName;

    @Pattern(regexp = "^998([0-9][012345789]|[0-9][125679]|7[01234569])[0-9]{7}$",
            message = "Invalid Uzbekistan phone number")
    private String phoneNumber;

    private Long categoryId;

    private String password;

    private Long fileId;
}
