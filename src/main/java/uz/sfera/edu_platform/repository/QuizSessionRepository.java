package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Quiz;
import uz.sfera.edu_platform.entity.QuizSession;
import uz.sfera.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepository extends JpaRepository<QuizSession,Long> {
    QuizSession findByUserAndQuizAndActiveTrue(User user, Quiz quiz);
}
