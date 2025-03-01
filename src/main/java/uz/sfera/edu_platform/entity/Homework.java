package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Homework extends AbsEntity {

    @Column(nullable = false)
    private String answer;

    private byte ball; // 5 ball // byte

    @OneToOne
    private File file;

    @ManyToOne
    private User student;

    @ManyToOne
    private Task task;

    private byte checked; // byte
}