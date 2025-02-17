package com.example.edu_platform.payload.req;

import com.example.edu_platform.entity.File;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LessonRequest {
    @NotBlank(message = "Dars nomi bo'sh bo'lishi mumkin emas")
    private String name;
    @NotBlank(message = "Tavsif bo‘sh bo‘lishi mumkin emas")
    private String description;
    private Long moduleId;
    private String videoLink;
    private List<Long> fileIds;
}
