package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends AbsEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double coursePrice;

    private byte duration; // bayt

    private byte active;

    @OneToOne
    private File file;

}
