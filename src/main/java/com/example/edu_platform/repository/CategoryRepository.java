package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select coalesce(count (c) , 0)  from Category c where c.active is true ")
    Integer countAllByCategory();
}
