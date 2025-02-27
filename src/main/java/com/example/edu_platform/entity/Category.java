package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends AbsEntity {

    private String name;
    private String description;
    private Double coursePrice;
    private Integer duration;
    private Boolean active;
    private Boolean deleted;
    @OneToOne
    private File file;
}
