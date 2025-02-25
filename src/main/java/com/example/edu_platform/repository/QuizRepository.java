package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findByLessonId(Long lessonId);
    @Query("SELECT q.id FROM Quiz q WHERE q.lesson.id IN :lessonIds")
    List<Long> findQuizIdsByLessonIds(@Param("lessonIds") List<Long> lessonIds);

}
