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

    private String name;
    private String description;
    private String videoLink;
    @ManyToOne
    private Module module;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<File> files;
    private boolean deleted;
}
