package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByQuizId(Long quizId);
    List<Question> findRandomQuestionsByQuizId(Long quizId);
    int countByQuizId(Long quizId);
}
