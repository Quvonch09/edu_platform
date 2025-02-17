package com.example.edu_platform.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private List<Long> categoryId;
    private Integer groupCount;
    private Boolean active;
}
