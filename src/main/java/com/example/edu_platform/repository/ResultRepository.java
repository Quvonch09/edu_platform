package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserId(Long userId);
    Page<Result> findByUserId(Long userId, Pageable pageable);
    Page<Result> findByQuizIdIn(List<Long> quizIds, Pageable pageable);

}
