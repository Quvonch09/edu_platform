package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findByLessonId(Long lessonId);
}
