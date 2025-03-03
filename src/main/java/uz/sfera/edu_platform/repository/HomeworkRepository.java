package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Homework;
import uz.sfera.edu_platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeworkRepository extends JpaRepository<Homework,Long> {
    long countByStudentId(Long studentId);

    @Query("SELECT COALESCE(SUM(h.ball), 0) FROM Homework h WHERE h.student = :student")
    int sumBallByStudent(@Param("student") User student);

    @Query(value = "select count(h.ball) from homework h where h.student_id = ?1 and h.checked = 1", nativeQuery = true)
    Double countByBall(Long studentId);

    Page<Homework> findByCheckedAndStudentId(byte isChecked, Long studentId, Pageable pageable);

    Page<Homework> findByCheckedAndTaskId(byte isChecked, Long taskId, Pageable pageable);

}

