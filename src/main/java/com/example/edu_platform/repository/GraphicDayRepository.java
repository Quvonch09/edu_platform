package com.example.edu_platform.repository;

import com.example.edu_platform.entity.GraphicDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public interface GraphicDayRepository extends JpaRepository<GraphicDay, Long> {
    boolean existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(Long roomId, LocalTime startTime,
                                                                      LocalTime endTime);

    Optional<GraphicDay> findByRoomId(Long roomId);
}
