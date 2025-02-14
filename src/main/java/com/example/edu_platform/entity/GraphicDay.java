package com.example.edu_platform.entity;

import com.example.edu_platform.entity.enums.WeekDay;
import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private WeekDay weekDay;
    @ManyToOne
    private Room room;
}
