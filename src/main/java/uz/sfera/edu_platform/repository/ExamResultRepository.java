package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.ExamResult;

import java.time.Month;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {

    @Query(value = """
    SELECT ex.* 
    FROM exam_result ex
    LEFT JOIN groups g ON g.id = ex.group_id 
    LEFT JOIN groups_students gs ON gs.group_id = g.id AND gs.students_id = ex.student_id
    WHERE (:teacherId IS NULL OR g.teacher_id = :teacherId)
      AND (:month IS NULL OR ex.month = :month)
      AND (:groupId IS NULL OR ex.group_id = :groupId)
      AND (:studentId IS NULL OR ex.student_id = :studentId)
    ORDER BY ex.id DESC
    """, nativeQuery = true)
    Page<ExamResult> searchResult(@Param("teacherId") Long teacherId,
                                  @Param("month") String month,
                                  @Param("groupId") Long groupId,
                                  @Param("studentId") Long studentId,
                                  Pageable pageable);



    @Query(value = "select ex.* from exam_result ex where \n" +
            "(:month IS NULL OR ex.month = :month) and ex.student_id = :studentId order by ex.id desc", nativeQuery = true)
    Page<ExamResult> searchResultStudent(
                                          @Param("month") String month,
                                          @Param("studentId") Long studentId, Pageable pageable);
}
