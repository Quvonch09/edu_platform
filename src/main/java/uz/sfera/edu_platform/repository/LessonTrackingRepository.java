package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.LessonTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonTrackingRepository extends JpaRepository<LessonTracking,Long> {
    List<LessonTracking> findByGroupId(Long groupId);

    boolean existsByLessonIdAndGroupId(Long lessonId, Long groupId);
}
