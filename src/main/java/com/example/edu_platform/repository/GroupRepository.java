package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select  coalesce( count (g) , 0) from Group g where g.active = true ")
    Integer countAllByGroup();
}
