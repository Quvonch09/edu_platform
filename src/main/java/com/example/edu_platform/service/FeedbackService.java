package com.example.edu_platform.service;

import com.example.edu_platform.entity.Feedback;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.exception.NotFoundException;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.FeedbackDto;
import com.example.edu_platform.repository.FeedbackRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public ApiResponse leaveFeedback(FeedbackDto feedbackDto) {
        User teacher = userRepository.findById(feedbackDto.getTeacherId())
                .orElseThrow(() -> new NotFoundException("teacher not found"));
        Feedback feedback = Feedback.builder()
                .feedback(feedbackDto.getFeedback())
                .rating(feedbackDto.getRating())
                .teacher(teacher)
                .build();
        feedbackRepository.save(feedback);
        return new ApiResponse("Feedback left successfully");
    }

    public ApiResponse editFeedback(String comment, int rating, Long feedbackId, User user) {
        Feedback feedback = feedbackRepository.findByIdAndCreatedBy(feedbackId, user.getId())
                .orElseThrow(() -> new NotFoundException("Feedback not found"));
        feedback.setFeedback(comment);
        feedback.setRating(rating);
        feedbackRepository.save(feedback);
        return new ApiResponse("Feedback updated successfully");
    }



}
