package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.ExamResult;

import java.time.Month;

public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {
    Page<ExamResult> findByMonth(Month month,Pageable pageable);

    Page<ExamResult> findByStudentId(Long studentId,Pageable pageable);

    Page<ExamResult> findByMonthAndStudentId(Month month, Long student_id, Pageable pageable);


    @Query(value = "select * from exam_result  er where\n" +
            "            (:month IS NULL OR er.month = :month)\n" +
            "            and (:studentId IS NULL OR er.student_id = :studentId)  order by er.id desc", nativeQuery = true)
    Page<ExamResult> searchResult(@Param("month") String month, @Param("studentId") Long studentId, Pageable pageable);
}
