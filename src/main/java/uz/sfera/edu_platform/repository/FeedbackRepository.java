package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.sfera.edu_platform.payload.res.ResFeedback;
import uz.sfera.edu_platform.payload.res.ResFeedbackCount;

import java.util.List;
import java.util.Optional;


@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByIdAndCreatedBy(long id, long createdBy);

    boolean existsByCreatedByAndTeacherId(long studentId, long teacherId);

    boolean existsByCreatedByAndLessonId(long studentId, long lessonId);

    boolean existsByCreatedByAndQuizId(long studentId, long quizId);

    Page<Feedback> getAllByTeacherId(Long teacherId, Pageable pageable);

    Page<Feedback> getAllByLessonId(Long lessonId, Pageable pageable);

    Page<Feedback> getAllByQuizId(Long quizId, Pageable pageable);

    Page<Feedback> getAllByCreatedBy(Long studentId, Pageable pageable);


    @Query(value = "select count(f.*) as feedbackCount, coalesce(avg(f.rating),0) as feedbackBall, u.full_name as teacherName " +
            "from feedback f join users u on u.id = f.teacher_id\n" +
            "           where teacher_id =:teacherId group by u.full_name", nativeQuery = true)
    ResFeedbackCount findAllByTeacher(@Param("teacherId") Long teacherId);

    @Query(value = "select count(f.*) as feedbackCount, coalesce(avg(f.rating),0) as feedbackBall, u.full_name as teacherName " +
            "from feedback f left join users u on u.id = f.teacher_id\n" +
            "                    where f.lesson_id IS NOT NULL group by u.full_name", nativeQuery = true)
    ResFeedbackCount findAllByLesson();

    @Query(value = "select count(f.*) as feedbackCount, coalesce(avg(f.rating),0) as feedbackBall, u.full_name as teacherName " +
            "from feedback f left join users u on u.id = f.teacher_id\n" +
            "                    where f.quiz_id IS NOT NULL group by u.full_name", nativeQuery = true)
    ResFeedbackCount findAllByQuiz();
}
