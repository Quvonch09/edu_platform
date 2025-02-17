package com.example.edu_platform.payload.res;

import com.example.edu_platform.payload.AttendDto;
import lombok.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResAttend {
    private Long studentId;
    private String fullName;
    private List<AttendDto> attendList;
}
