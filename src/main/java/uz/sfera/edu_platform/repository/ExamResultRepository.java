package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.sfera.edu_platform.entity.ExamResult;

import java.time.Month;

public interface ExamResultRepository extends JpaRepository<ExamResult,Long> {
    Page<ExamResult> findByMonth(Month month,Pageable pageable);

    Page<ExamResult> findByStudentId(Long studentId,Pageable pageable);

    Page<ExamResult> findByMonthAndStudentId(Month month, Long student_id, Pageable pageable);
}
