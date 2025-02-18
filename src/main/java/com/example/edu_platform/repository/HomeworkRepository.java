package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    @Query(value = "select count(h.ball) from homework h where h.student_id = ?1 and h.checked = true", nativeQuery = true)
    Double countByBall(Long studentId);
}
