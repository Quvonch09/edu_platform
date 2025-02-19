package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Quiz;
import com.example.edu_platform.entity.QuizSession;
import com.example.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepository extends JpaRepository<QuizSession,Long> {
    QuizSession findByUserAndQuizAndActiveTrue(User user, Quiz quiz);
}
