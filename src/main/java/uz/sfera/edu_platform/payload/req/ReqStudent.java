package uz.sfera.edu_platform.payload.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqStudent {
    private String fullName;
    private String phoneNumber;
    private Integer age;
    private Long groupId;
    private String parentPhoneNumber;
    private Long fileId;
    private String password;
}
