package com.example.edu_platform.payload.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTask {
    private String title;
    private Long fileId;
    private Long lessonId;
}
