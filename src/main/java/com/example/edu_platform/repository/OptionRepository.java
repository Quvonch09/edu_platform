package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,Long> {
    @Query("SELECT a FROM Option a WHERE a.question.id IN :questionIds AND a.correct = true")
    List<Option> findCorrectAnswersByQuestionIds(@Param("questionIds") List<Long> questionIds);

    List<Option> findByQuestionId(Long questionId);
}
