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

    private Integer duration; // byte

    private boolean active; // byte ga utqazish kerak

    @OneToOne
    private File file;

//    private byte active; // 0 = false , 1 = ture
}
