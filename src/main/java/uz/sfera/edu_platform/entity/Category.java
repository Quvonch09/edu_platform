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

    private String name;
    private String description;
    private Double coursePrice;
    private Integer duration;
    private Boolean active;

    @OneToOne
    private File file;

//    private byte active; // 0 = false , 1 = ture
}
