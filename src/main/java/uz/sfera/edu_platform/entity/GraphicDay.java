package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<DayOfWeek> weekDay;

    @ManyToOne
    private Room room;
}
