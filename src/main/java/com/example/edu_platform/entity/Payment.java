package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment extends AbsEntity {
    private Double price;
}
