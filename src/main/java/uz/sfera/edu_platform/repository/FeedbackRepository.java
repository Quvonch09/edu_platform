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
import uz.sfera.edu_platform.payload.res.ResFeedbackDTO;

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
            "           where u.id =:teacherId group by u.full_name", nativeQuery = true)
    ResFeedbackCount findAllByTeacher(@Param("teacherId") Long teacherId);


    @Query(value = "select f.id, f.feedback, s.full_name as studentName, f.created_at as feedbackTime, f.rating, null, null  from feedback f left join users s on s.id = f.student_id " +
            "join users u on u.id = f.teacher_id\n" +
            "           where teacher_id =:teacherId " +
            "group by \n" +
            "                u.full_name, \n" +
            "                f.rating, \n" +
            "                s.full_name, \n" +
            "                f.created_at, \n" +
            "                f.created_by, \n" +
            "                f.id, \n" +
            "                f.lesson_id, \n" +
            "                quiz_id, \n" +
            "                student_id, \n" +
            "                teacher_id, \n" +
            "                f.updated_at, \n" +
            "                f.updated_by, \n" +
            "                f.feedback", nativeQuery = true)
    Page<ResFeedbackDTO> findFeedbackForTeacher(@Param("teacherId") Long teacherId,Pageable pageable);


    @Query(value = """
        select count(f.*) as feedbackCount, coalesce(avg(f.rating),0) as feedbackBall, u.full_name as teacherName 
            from feedback f  
             left join  lesson l  on  f.lesson_id =l.id 
            left join users u on u.id  = l.created_by 
            where f.lesson_id IS NOT NULL and  u.id = :teacherId group by u.full_name
            """
            , nativeQuery = true)
    ResFeedbackCount findAllByLesson(@Param("teacherId") Long teacherId) ;


    @Query(value = """
        select f.id, f.feedback, s.full_name as studentName, f.created_at as feedbackTime, f.rating, null, l.id as lessonId from feedback f  
            left join users s on s.id = f.student_id 
             left join  lesson l  on  f.lesson_id =l.id 
            left join users u on u.id  = l.created_by 
            where f.lesson_id IS NOT NULL and  u.id = :teacherId
            group by 
                u.full_name, 
                f.rating, 
                s.full_name,
                f.created_at, 
                f.created_by, 
                f.id, 
                l.id, 
                f.quiz_id, 
                student_id, 
                teacher_id, 
                f.updated_at, 
                f.updated_by, 
                f.feedback
            """
            , nativeQuery = true)
    Page<ResFeedbackDTO> findFeedbackForLesson(@Param("teacherId") Long teacherId, Pageable pageable); ;


    @Query(value = """
            select count(f.*) as feedbackCount, coalesce(avg(f.rating),0) as feedbackBall, u.full_name as teacherName 
            from feedback f 
            left join quiz q on q.id = f.quiz_id 
            left join  lesson l   on l.id = q.lesson_id
            left join users u on u.id = l.created_by 
                               where f.quiz_id IS NOT NULL and u.id = :teacherId group by u.full_name""", nativeQuery = true)
    ResFeedbackCount findAllByQuiz(@Param("teacherId") Long teacherId);


    @Query(value = """
            select f.id, f.feedback, s.full_name as studentName, f.created_at as feedbackTime, f.rating, q.id as quizId, null from feedback f
            left join quiz q on q.id = f.quiz_id
            left join users s on s.id = f.student_id
            left join  lesson l   on l.id = q.lesson_id
            left join users u on u.id = l.created_by
            where f.quiz_id IS NOT NULL and u.id = :teacherId
            group by 
                u.full_name, 
                f.rating, 
                s.full_name,
                f.created_at, 
                f.created_by, 
                f.id, 
                l.id, 
                q.id, 
                student_id, 
                teacher_id, 
                f.updated_at, 
                f.updated_by, 
                f.feedback""", nativeQuery = true)
    Page<ResFeedbackDTO> findFeedbackForQuiz(@Param("teacherId") Long teacherId, Pageable pageable);


}
