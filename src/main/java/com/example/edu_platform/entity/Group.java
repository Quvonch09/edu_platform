package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "groups")
public class Group extends AbsEntity {
    private String name;
    @ManyToOne
    private User teacher;
    private Boolean active;
    @ManyToOne
    private Category category;
    @ManyToMany
    private List<User> studentList;
    private LocalDate endDate;
}
