package com.example.edu_platform.entity;


import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Module extends AbsEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Category category;

    private boolean deleted;
}
