package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    List<Lesson> findByModuleId(Long moduleId);
    long countByDeletedFalse();
    long countByDeletedTrue();
    long countByModuleIdAndDeletedFalse(Long moduleId);
    long countByModuleIdAndDeletedTrue(Long moduleId);
}
