package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    boolean existsByName(String name);
    Page<Module> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Module> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
