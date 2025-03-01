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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Task task;

    private byte checked; // byte
}