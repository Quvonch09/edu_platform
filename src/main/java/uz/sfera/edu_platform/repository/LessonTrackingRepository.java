package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.Lesson;
import uz.sfera.edu_platform.entity.LessonTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonTrackingRepository extends JpaRepository<LessonTracking,Long> {
    @Query("""
    SELECT lt.lesson
    FROM LessonTracking lt
    WHERE lt.group.id = :groupId
      AND lt.lesson.deleted = 0
""")
    List<Lesson> findOpenLessonsByGroupId(@Param("groupId") Long groupId);

    boolean existsByLessonIdAndGroupId(Long lessonId, Long groupId);
}
