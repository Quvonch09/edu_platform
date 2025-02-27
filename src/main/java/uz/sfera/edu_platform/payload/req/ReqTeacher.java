package uz.sfera.edu_platform.payload.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqTeacher {
    private String fullName;
    private String phoneNumber;
    private Long categoryId;
    private String password;
    private Long fileId;
}
