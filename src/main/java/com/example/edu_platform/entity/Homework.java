package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Homework extends AbsEntity {
    private String answer;
    private Integer ball; // 5 ball
    @OneToOne
    private File file;
    @ManyToOne
    private User student;
    private Boolean checked;
    @ManyToOne
    private Task task;
}
