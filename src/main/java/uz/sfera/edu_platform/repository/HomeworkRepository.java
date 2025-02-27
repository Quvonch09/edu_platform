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

    @Query(value = "select count(h.ball) from homework h where h.student_id = ?1 and h.checked = true", nativeQuery = true)
    Double countByBall(Long studentId);

    Page<Homework> findByCheckedAndStudentId(boolean isChecked, Long studentId, Pageable pageable);
    Page<Homework> findByCheckedAndTaskId(boolean isChecked, Long taskId, Pageable pageable);

}

