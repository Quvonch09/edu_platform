package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.GraphicDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface GraphicDayRepository extends JpaRepository<GraphicDay, Long> {
    boolean existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(Long roomId, LocalTime startTime,
                                                                      LocalTime endTime);

    @Query(value = "select gd.* from room r join graphic_day gd on gd.id = r.graphic_day_id limit 1", nativeQuery = true)
    Optional<GraphicDay> findByRoomId(Long roomId);

    @Query(value = "select gd.* from graphic_day gd join groups g on gd.id = g.days_id where g.id = ?1 limit 1", nativeQuery = true)
    Optional<GraphicDay> findGraphicDay(Long id);


    @Query(value = "SELECT EXISTS ( " +
            "SELECT 1 FROM graphic_day g " +
            "JOIN groups gr ON gr.days_id = g.id " +  // Group jadvalidagi graphicDay bilan bog‘lash
            "WHERE g.room_id = :roomId " +
            "AND (:startTime >= g.start_time AND :endTime <= g.end_time) " +
            ") ", nativeQuery = true)
    boolean existsByGraphicDayInGroup(@Param("roomId") Long roomId,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime);



}
