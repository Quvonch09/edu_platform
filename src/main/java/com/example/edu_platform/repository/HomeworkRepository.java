package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Homework;
import com.example.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework,Long> {
    List<Homework> findByCheckedAndStudentId(boolean isChecked,Long studentId);
    List<Homework> findByCheckedAndTaskId(boolean isChecked,Long taskId);
    long countByStudentId(Long studentId);
    @Query("SELECT COALESCE(SUM(h.ball), 0) FROM Homework h WHERE h.student = :student")
    int sumBallByStudent(@Param("student") User student);

    @Query(value = "select count(h.ball) from homework h where h.student_id = ?1 and h.checked = true", nativeQuery = true)
    Double countByBall(Long studentId);
}

