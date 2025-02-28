package uz.sfera.edu_platform.entity;

import jakarta.persistence.Column;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    private Integer ball; // 5 ball // byte

    @OneToOne
    private File file;

    @ManyToOne
    private User student;

    @ManyToOne
    private Task task;

    private boolean checked; // byte
}