package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Integer countByTeacherId(Long teacherId);

    @Query(value = "select * from groups g join groups_student_list gsl on gsl.student_list_id = ?1", nativeQuery = true)
    Optional<Group> findByStudentId(Long studentId);

    @Query(value = "delete from groups_student_list where student_list_id = ?1", nativeQuery = true)
    void deleteUserGroupStudentList(Long studentId);
}
