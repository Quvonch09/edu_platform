package com.example.edu_platform.payload.res;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResAdminStatistic {

    private Integer teacherCount;
    private Integer studentCount;
    private Integer groupCount;
    private Integer categoryCount;
    private Integer paidAllCount;
    private Integer paidCount;
}
