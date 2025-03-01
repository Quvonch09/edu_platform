package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Room;
import uz.sfera.edu_platform.payload.res.ResRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByName(String name);

    @Query(value = "select r.id, r.name, r.color, r.start_time as startTime, r.end_time as endTime from room r\n" +
            "            where (?1 IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%')))\n" +
            "            and (?2 IS NULL OR LOWER(r.color) LIKE LOWER(CONCAT('%', ?2, '%')))",
            nativeQuery = true)
    Page<ResRoom> getAllRooms(String name, String color, Pageable pageable);
}