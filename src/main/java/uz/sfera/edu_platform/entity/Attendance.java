package uz.sfera.edu_platform.entity;


import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance extends AbsEntity {

    private Boolean attendance;
    private LocalDate date;

    @ManyToOne
    private Group group;

    @ManyToOne
    private User student;
}
