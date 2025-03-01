package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.template.AbsEntity;
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

    @Column(nullable = false)
    private String name;

    private String description;

    private String videoLink;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Module module;

    @ManyToMany
    private List<File> files;

    private byte deleted; // byte
}
