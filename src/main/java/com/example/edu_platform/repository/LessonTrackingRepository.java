package com.example.edu_platform.repository;

import com.example.edu_platform.entity.LessonTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonTrackingRepository extends JpaRepository<LessonTracking,Long> {
    List<LessonTracking> findByGroupId(Long groupId);
    List<LessonTracking> findByLessonId(Long lessonId);

    boolean existsByLessonIdAndGroupId(Long lessonId, Long groupId);

    @Query("SELECT lt.lesson.id FROM LessonTracking lt WHERE lt.group.id = :groupId")
    List<Long> findLessonIdsByGroupId(@Param("groupId") Long groupId);

}
