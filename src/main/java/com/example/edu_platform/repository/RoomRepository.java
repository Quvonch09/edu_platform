package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Room;
import com.example.edu_platform.payload.res.ResRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);

    @Query(value = "select r.id, r.name, r.color, g.start_time as startTime, g.end_time as endTime " +
            "from room r join graphic_day g on g.id = r.graphic_day_id " +
            "where (?1 IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "and (?2 IS NULL OR LOWER(r.color) LIKE LOWER(CONCAT('%', ?2, '%')))",
            nativeQuery = true)
    Page<ResRoom> getAllRooms(String name, String color, Pageable pageable);

}
