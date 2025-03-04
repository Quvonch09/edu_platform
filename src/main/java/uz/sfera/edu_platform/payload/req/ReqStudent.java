package uz.sfera.edu_platform.payload.req;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqStudent {

    @NotBlank(message = "Bo‘sh bo‘lishi mumkin emas")
    @Size(max = 32, message = "Ism 32 ta belgidan oshmasligi kerak")
    private String fullName;

    @NotBlank(message = "Bo‘sh bo‘lishi mumkin emas")
    @Pattern(regexp = "^998([0-9][012345789]|[0-9][125679]|7[01234569])[0-9]{7}$",
            message = "Noto‘g‘ri O‘zbekiston telefon raqami")
    private String phoneNumber;

    @NotNull(message = "Bo‘sh bo‘lishi mumkin emas")
    @Min(value = 1, message = "Yosh 1 dan kichik bo‘lishi mumkin emas")
    @Max(value = 99, message = "Yosh 99 dan katta bo‘lishi mumkin emas")
    private Integer age;

    @NotNull(message = "Bo‘sh bo‘lishi mumkin emas")
    private Long groupId;

    @NotBlank(message = "Bo‘sh bo‘lishi mumkin emas")
    private String parentPhoneNumber;

    private Long fileId;

    @NotBlank(message = "Bo‘sh bo‘lishi mumkin emas")
    @Size(min = 3, max = 10, message = "Parol kamida 3 ta va ko‘pi bilan 10 ta belgidan iborat bo‘lishi kerak")
    private String password;
}
