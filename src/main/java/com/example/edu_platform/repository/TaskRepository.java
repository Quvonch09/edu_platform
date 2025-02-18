package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByLessonId(Long lessonId);
}
