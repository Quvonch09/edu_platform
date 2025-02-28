package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private User teacher;

    @ManyToOne
    private Category category;

    @ManyToMany
    private List<User> students;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToOne
    private GraphicDay graphicDay;

    private boolean active; //enum status (ochiq, yopildi, tugatildi)
}