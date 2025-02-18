package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Integer countByTeacherId(Long teacherId);

    @Query(value = "select * from groups g join groups_students gsl on gsl.students_id = ?1", nativeQuery = true)
    Optional<Group> findByStudentId(Long studentId);

    @Query(value = "delete from groups_students where students_id = ?1", nativeQuery = true)
    void deleteUserGroupStudentList(Long studentId);
}
