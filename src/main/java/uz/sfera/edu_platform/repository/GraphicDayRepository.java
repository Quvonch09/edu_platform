package uz.sfera.edu_platform.repository;

import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.GraphicDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GraphicDayRepository extends JpaRepository<GraphicDay, Long> {



    @Query(value = """
    SELECT gd.* 
    FROM graphic_day gd 
    JOIN groups g ON gd.id = g.days_id 
    WHERE g.id IN :groupIds
""", nativeQuery = true)
    List<GraphicDay> findAllByGroupIds(@Param("groupIds") List<Long> groupIds);


    @Query(value = """
    SELECT EXISTS (
        SELECT 1 
        FROM graphic_day g 
        JOIN groups gr ON gr.days_id = g.id
        WHERE g.room_id = :roomId
        AND NOT (
            g.end_time <= :startTime OR
            g.start_time >= :endTime
        )
    )
    """, nativeQuery = true)
    boolean existsByGraphicDayInGroup(@Param("roomId") Long roomId,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime);
}
