package com.example.edu_platform.repository;

import com.example.edu_platform.entity.QuizSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSettingsRepository extends JpaRepository<QuizSettings,Long> {
    QuizSettings findByQuizId(Long quizId);
}
