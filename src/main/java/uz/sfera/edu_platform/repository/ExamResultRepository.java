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

    @Query(value = "select ex.* from exam_result ex left join groups g on g.teacher_id = :teacherId \n" +
            "    left join groups_students gs on g.id = gs.group_id\n" +
            "and gs.students_id  = ex.student_id where \n" +
            "(:month IS NULL OR ex.month = :month) and " +
            "(:studentId IS NULL OR ex.student_id = :studentId) order by ex.id desc", nativeQuery = true)
    Page<ExamResult> searchResult(@Param("teacherId") Long teacherId,
                                  @Param("month") String month,
                                  @Param("studentId") Long studentId, Pageable pageable);

    @Query(value = "select ex.* from exam_result ex where \n" +
            "(:month IS NULL OR ex.month = :month) and ex.student_id = :studentId order by ex.id desc", nativeQuery = true)
    Page<ExamResult> searchResultStudent(
                                          @Param("month") String month,
                                          @Param("studentId") Long studentId, Pageable pageable);
}
