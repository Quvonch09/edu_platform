package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Feedback;
import com.example.edu_platform.payload.FeedbackDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByIdAndCreatedBy(long id, long createdBy);
}
