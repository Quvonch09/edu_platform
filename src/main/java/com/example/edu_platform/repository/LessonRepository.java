package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Lesson;
import com.example.edu_platform.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    List<Lesson> findByModuleId(Long moduleId);
    long countByDeletedFalse();
    long countByDeletedTrue();
    long countByModuleIdAndDeletedFalse(Long moduleId);
    long countByModuleIdAndDeletedTrue(Long moduleId);
    Page<Lesson> findByName(String name, Pageable pageable);


    @Query(value = "select count(l.*) from lesson l join module m on l.module_id = m.id " +
            " join category c on c.id = m.category_id", nativeQuery = true)
    Integer countLessonsByCategoryId(Long categoryId);

}
