package com.example.edu_platform.repository;

import com.example.edu_platform.entity.LessonTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonTrackingRepository extends JpaRepository<LessonTracking,Long> {
    List<LessonTracking> findByGroupId(Long groupId);
    List<LessonTracking> findByLessonId(Long lessonId);
}
