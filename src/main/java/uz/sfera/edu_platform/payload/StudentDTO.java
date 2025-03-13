package uz.sfera.edu_platform.payload;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private Long groupId;
    private LocalDateTime startStudyDate;
    private Integer age;
    private String status;
    private Boolean isPay;
    private String parentPhoneNumber;
    private Double score;
    private String departureDescription;
}
