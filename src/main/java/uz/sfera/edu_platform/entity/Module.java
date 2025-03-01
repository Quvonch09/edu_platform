package uz.sfera.edu_platform.entity;


import uz.sfera.edu_platform.entity.template.AbsEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Category category;

    private byte deleted; // byte
}
