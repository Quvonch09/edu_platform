package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqStudent {

    @NotBlank(message = "Bush bulmasin")
    private String fullName;

    @NotBlank(message = "Bush bulmasin")
    @Pattern(regexp = "^998([0-9][012345789]|[0-9][125679]|7[01234569])[0-9]{7}$",
            message = "Invalid Uzbekistan phone number")
    private String phoneNumber;

    @Min(value = 1, message = "Yosh 1 dan kichik bo'lishi mumkin emas")
    private Integer age;

    @NotNull(message = "Bush bulmasin")
    private Long groupId;

    @NotBlank(message = "Bush bulmasin")
    @Pattern(regexp = "^998([0-9][012345789]|[0-9][125679]|7[01234569])[0-9]{7}$",
            message = "Invalid Uzbekistan phone number")
    private String parentPhoneNumber;

    private Long fileId;

    @NotBlank(message = "Bush bulmasin")
    @Size(min = 3, message = "Parol kamida 3 ta belgidan iborat bo'lishi kerak")
    private String password;
}
