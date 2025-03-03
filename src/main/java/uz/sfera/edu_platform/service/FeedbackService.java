package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Feedback;
import uz.sfera.edu_platform.entity.Lesson;
import uz.sfera.edu_platform.entity.Quiz;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.exception.BadRequestException;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.mapper.FeedbackMapper;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.FeedbackDto;
import uz.sfera.edu_platform.payload.ResponseFeedback;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.FeedbackRepository;
import uz.sfera.edu_platform.repository.LessonRepository;
import uz.sfera.edu_platform.repository.QuizRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final FeedbackMapper feedbackMapper;


    public ApiResponse leaveFeedback(FeedbackDto feedbackDto, User student, FeedbackType type) {
        Function<Long, Object> entityFinder = switch (type) {
            case TEACHER -> id -> userRepository.findById(id).orElseThrow(() -> new NotFoundException("teacher not found"));
            case LESSON -> id -> lessonRepository.findById(id).orElseThrow(() -> new NotFoundException("lesson not found"));
            case QUIZ -> id -> quizRepository.findById(id).orElseThrow(() -> new NotFoundException("quiz not found"));
        };

        long entityId = switch (type) {
            case TEACHER -> feedbackDto.getTeacherId();
            case LESSON -> feedbackDto.getLessonId();
            case QUIZ -> feedbackDto.getQuizId();
        };

        Object entity = entityFinder.apply(entityId);

        boolean exist = switch (type) {
            case TEACHER -> feedbackRepository.existsByCreatedByAndTeacherId(student.getId(), entityId);
            case LESSON -> feedbackRepository.existsByCreatedByAndLessonId(student.getId(), entityId);
            case QUIZ -> feedbackRepository.existsByCreatedByAndQuizId(student.getId(), entityId);
        };

        if (exist) throw new BadRequestException("feedback already exist");

        Feedback feedback = new Feedback();
        saveBuilderFeedback(feedbackDto, feedback,
                type == FeedbackType.QUIZ ? (Quiz) entity : null,
                type == FeedbackType.LESSON ? (Lesson) entity : null,
                student,
                type == FeedbackType.TEACHER ? (User) entity : null
        );

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

    public ApiResponse getAllFeedback(Long entityId, int page, int size, FeedbackType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = switch (type) {
            case TEACHER -> feedbackRepository.getAllByTeacherId(entityId, pageable);
            case LESSON -> feedbackRepository.getAllByLessonId(entityId, pageable);
            case QUIZ -> feedbackRepository.getAllByQuizId(entityId, pageable);
        };
        return new ApiResponse(toResponseFeedback(page, size, feedbacks));
    }

    private ResPageable toResponseFeedback(int page, int size, Page<Feedback> feedbacks) {
        List<ResponseFeedback> responseFeedbacks = feedbacks.map(feedbackMapper::toResponseFeedback).toList();
        return new ResPageable(page, size, feedbacks.getTotalPages(), feedbacks.getTotalElements(), responseFeedbacks);
    }

    public enum FeedbackType {
        TEACHER, LESSON, QUIZ
    }


    public void saveBuilderFeedback(FeedbackDto feedbackDto, Feedback feedback, Quiz quiz, Lesson lesson, User student, User teacher) {
        feedback.setFeedback(feedbackDto.getFeedback());
        feedback.setRating(feedbackDto.getRating());
        feedback.setQuiz(quiz);
        feedback.setLesson(lesson);
        feedback.setTeacher(teacher);
        feedback.setStudent(student);
    }
}



//    public ApiResponse leaveFeedbackToTeacher(FeedbackDto feedbackDto, User student) {
//        User teacher = userRepository.findById(feedbackDto.getTeacherId())
//                .orElseThrow(() -> new NotFoundException("teacher not found"));
//
//        boolean exist = feedbackRepository.existsByCreatedByAndTeacherId(student.getId(), teacher.getId());
//        if(exist) throw new BadRequestException("feedback already exist");
//
//        Feedback feedback = new Feedback();
//        saveBuilderFeedback(feedbackDto, feedback, null, null, student, teacher);
//        feedbackRepository.save(feedback);
//        return new ApiResponse("Feedback left successfully");
//    }
//
//    public ApiResponse leaveFeedbackToLesson(FeedbackDto feedbackDto, User student) {
//        Lesson lesson = lessonRepository.findById(feedbackDto.getLessonId())
//                .orElseThrow(() -> new NotFoundException("lesson not found"));
//
//        boolean exist = feedbackRepository.existsByCreatedByAndLessonId(student.getId(), lesson.getId());
//        if(exist) throw new BadRequestException("feedback already exist");
//
//        Feedback feedback = new Feedback();
//        saveBuilderFeedback(feedbackDto, feedback, null, lesson, student, null);
//        feedbackRepository.save(feedback);
//        return new ApiResponse("Feedback left successfully");
//    }
//
//    public ApiResponse leaveFeedbackToQuiz(FeedbackDto feedbackDto, User student) {
//        Quiz quiz = quizRepository.findById(feedbackDto.getQuizId())
//                .orElseThrow(() -> new NotFoundException("quiz not found"));
//
//        boolean exist = feedbackRepository.existsByCreatedByAndQuizId(student.getId(), quiz.getId());
//        if(exist) throw new BadRequestException("feedback already exist");
//
//        Feedback feedback = new Feedback();
//        saveBuilderFeedback(feedbackDto, feedback, quiz, null, student, null);
//        feedbackRepository.save(feedback);
//        return new ApiResponse("Feedback left successfully");
//    }
//
//
//    public ApiResponse editFeedback(String comment, int rating, Long feedbackId, User user) {
//        Feedback feedback = feedbackRepository.findByIdAndCreatedBy(feedbackId, user.getId())
//                .orElseThrow(() -> new NotFoundException("Feedback not found"));
//
//        feedback.setFeedback(comment);
//        feedback.setRating(rating);
//        feedbackRepository.save(feedback);
//
//        return new ApiResponse("Feedback updated successfully");
//    }
//
//    public ApiResponse getAllByTeacherId(Long teacherId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Feedback> feedbacks = feedbackRepository.getAllByTeacherId(teacherId, pageable);
//        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
//        return new ApiResponse(responseFeedback);
//    }
//
//    public ApiResponse getAllByUserId(Long userId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Feedback> feedbacks = feedbackRepository.getAllByCreatedBy(userId, pageable);
//        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
//        return new ApiResponse(responseFeedback);
//    }
//
//    public ApiResponse getAllByLessonId(Long lessonId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Feedback> feedbacks = feedbackRepository.getAllByLessonId(lessonId, pageable);
//        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
//        return new ApiResponse(responseFeedback);
//    }
//
//    public ApiResponse getAllByQuizId(Long quizId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Feedback> feedbacks = feedbackRepository.getAllByQuizId(quizId, pageable);
//        ResPageable responseFeedback = toResponseFeedback(page, size, feedbacks);
//        return new ApiResponse(responseFeedback);
//    }
//
//
//    private ResPageable toResponseFeedback(int page, int size, Page<Feedback> feedbacks) {
//        List<ResponseFeedback> responseFeedbacks = new ArrayList<>();
//        for (Feedback feedback : feedbacks) {
//            responseFeedbacks.add(feedbackMapper.toResponseFeedback(feedback));
//        }
//        return ResPageable.builder()
//                .page(page)
//                .size(size)
//                .totalElements(feedbacks.getTotalElements())
//                .totalPage(feedbacks.getTotalPages())
//                .body(responseFeedbacks)
//                .build();
//    }