package com.example.edu_platform.payload.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqRoom {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String color;
    private List<Integer> weekDays;
    private String startTime;
    private String endTime;
}
