package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Lesson extends AbsEntity {

    private String name;
    private String description;
    private String videoLink;
    @ManyToOne
    private Module module;
    @ManyToMany
    private List<File> files;
    private boolean deleted;
}
