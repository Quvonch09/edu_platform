package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByIdAndCreatedBy(long id, long createdBy);

    boolean existsByCreatedByAndTeacherId(long studentId, long teacherId);
    boolean existsByCreatedByAndLessonId(long studentId, long lessonId);
    boolean existsByCreatedByAndQuizId(long studentId, long quizId);

    Page<Feedback> getAllByTeacherId(Long teacherId, Pageable pageable);

    Page<Feedback> getAllByCreatedBy(Long userId, Pageable pageable);

    Page<Feedback> getAllByLessonId(Long lessonId, Pageable pageable);

    Page<Feedback> getAllByQuizId(Long quizId, Pageable pageable);


}
