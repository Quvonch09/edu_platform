package com.example.edu_platform.service;

import com.example.edu_platform.entity.Feedback;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.exception.NotFoundException;
import com.example.edu_platform.mapper.FeedbackMapper;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.FeedbackDto;
import com.example.edu_platform.payload.ResponseFeedback;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.FeedbackRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final FeedbackMapper feedbackMapper;

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

    public ApiResponse getAllByTeacherId(Long teacherId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.getAllByTeacherId(teacherId, pageable);
        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
        return new ApiResponse(responseFeedback);
    }

    public ApiResponse getAllByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.getAllByCreatedBy(userId, pageable);
        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
        return new ApiResponse(responseFeedback);
    }

    public ApiResponse getAllByLessonId(Long lessonId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.getAllByLessonId(lessonId, pageable);
        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
        return new ApiResponse(responseFeedback);
    }

    public ApiResponse getAllByQuizId(Long quizId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.getAllByQuizId(quizId, pageable);
        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
        return new ApiResponse(responseFeedback);
    }


    private ResPageable toResponseFeedback(int page, int size, Page<Feedback> feedbacks) {
        List<ResponseFeedback> responseFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            responseFeedbacks.add(feedbackMapper.toResponseFeedback(feedback));
        }
        return ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(feedbacks.getTotalElements())
                .totalPage(feedbacks.getTotalPages())
                .body(responseFeedbacks)
                .build();
    }


}
