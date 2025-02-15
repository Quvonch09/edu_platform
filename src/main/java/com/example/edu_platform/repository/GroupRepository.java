package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Integer countByTeacherId(Long teacherId);
}
