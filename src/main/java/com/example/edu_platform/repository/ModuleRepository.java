package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    boolean existsByName(String name);
}
