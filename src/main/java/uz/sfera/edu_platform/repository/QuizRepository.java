package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findByLessonId(Long lessonId);
    Optional<Quiz> findByIdAndDeleted(Long id, byte deleted);
}