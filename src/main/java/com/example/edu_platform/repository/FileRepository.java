package com.example.edu_platform.repository;


import com.example.edu_platform.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
