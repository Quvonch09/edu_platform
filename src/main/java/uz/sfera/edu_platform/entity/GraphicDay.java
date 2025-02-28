package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.sfera.edu_platform.entity.enums.WeekDay;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class GraphicDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<WeekDay> weekDay;

    @ManyToOne
    private Room room;
}