package uz.sfera.edu_platform.repository;

import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Homework;
import uz.sfera.edu_platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Long> {
    long countByStudentId(Long studentId);

    @Query("SELECT COALESCE(SUM(h.ball), 0) FROM Homework h WHERE h.student = :student")
    int sumBallByStudent(@Param("student") User student);

    @Query(value = "select count(h.ball) from homework h where h.student_id = ?1 and h.checked = 1", nativeQuery = true)
    Double countByBall(Long studentId);

    @Query("SELECT h FROM Homework h WHERE "
            + "(:isChecked IS NULL OR h.checked = :isChecked) "
            + "AND (:studentId IS NULL OR h.student.id = :studentId) "
            + "AND (:taskId IS NULL OR h.task.id = :taskId)")
    Page<Homework> findByDynamicParams(
            @Param("isChecked") Byte isChecked,
            @Param("studentId") Long studentId,
            @Param("taskId") Long taskId,
            Pageable pageable);

    Page<Homework> findByCheckedAndStudentId(byte isChecked, Long studentId, Pageable pageable);

    Page<Homework> findByCheckedAndTaskId(byte isChecked, Long taskId, Pageable pageable);
}

