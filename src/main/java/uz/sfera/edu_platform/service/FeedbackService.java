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
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import uz.sfera.edu_platform.exception.BadRequestException;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.mapper.FeedbackMapper;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.FeedbackDto;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.ResponseFeedback;
import uz.sfera.edu_platform.payload.res.ResFeedback;
import uz.sfera.edu_platform.payload.res.ResFeedbackCount;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.FeedbackRepository;
import uz.sfera.edu_platform.repository.LessonRepository;
import uz.sfera.edu_platform.repository.QuizRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final FeedbackMapper feedbackMapper;

    public ApiResponse leaveFeedbackToTeacher(FeedbackDto feedbackDto, User student) {


        Long teacherId = userRepository.getTeacherId(student.getId());
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Teacher"))));
//        boolean exist = feedbackRepository.existsByCreatedByAndTeacherId(student.getId(), teacher.getId());
//        if(exist) {
//            throw new BadRequestException(new ApiResponse(ResponseError.ALREADY_EXIST("Feedback")).toString());
//        }
        Feedback feedback = Feedback.builder()
                .feedback(feedbackDto.getFeedback())
                .rating(feedbackDto.getRating())
                .teacher(teacher)
                .student(student)
                .build();
        feedbackRepository.save(feedback);
        return new ApiResponse("Feedback left successfully");
    }

    public ApiResponse leaveFeedbackToLesson(FeedbackDto feedbackDto, User student) {
        Lesson lesson = lessonRepository.findById(feedbackDto.getLessonId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Lesson"))));
//        boolean exist = feedbackRepository.existsByCreatedByAndLessonId(student.getId(), lesson.getId());
//        if(exist) {
//            throw new BadRequestException(new ApiResponse(ResponseError.ALREADY_EXIST("Feedback")).toString());
//        }
        Feedback feedback = Feedback.builder()
                .feedback(feedbackDto.getFeedback())
                .rating(feedbackDto.getRating())
                .lesson(lesson)
                .student(student)
                .build();
        feedbackRepository.save(feedback);
        return new ApiResponse("Feedback left successfully");
    }

    public ApiResponse leaveFeedbackToQuiz(FeedbackDto feedbackDto, User student) {
        Quiz quiz = quizRepository.findById(feedbackDto.getQuizId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Quiz"))));
//        boolean exist = feedbackRepository.existsByCreatedByAndQuizId(student.getId(), quiz.getId());
//        if(exist) {
//            throw new BadRequestException(new ApiResponse(ResponseError.ALREADY_EXIST("Feedback")).toString());
//        }
        Feedback feedback = Feedback.builder()
                .feedback(feedbackDto.getFeedback())
                .rating(feedbackDto.getRating())
                .quiz(quiz)
                .student(student)
                .build();
        feedbackRepository.save(feedback);
        return new ApiResponse("Feedback left successfully");
    }

    public ApiResponse editFeedback(String comment, int rating, Long feedbackId, User user) {
        Feedback feedback = feedbackRepository.findByIdAndCreatedBy(feedbackId, user.getId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Feedback"))));
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


    public ApiResponse getAllForCeo( int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        List<Long> teacherIds = userRepository.findAllByRole(Role.ROLE_TEACHER).stream()
                        .map(AbsEntity::getId).toList();

        List<ResFeedback> resFeedbacks = new ArrayList<>();
        for (Long teacherId : teacherIds) {
            User user = userRepository.findById(teacherId).get();
            ResFeedbackCount allByTeacher = feedbackRepository.findAllByTeacher(teacherId);
            ResFeedbackCount allByLesson = feedbackRepository.findAllByLesson(teacherId);
            ResFeedbackCount allByQuiz = feedbackRepository.findAllByQuiz(teacherId);

            ResFeedback resFeedback = ResFeedback.builder()
                    .teacherName(user.getFullName())
                    .countLesson(allByLesson != null ? allByLesson.getFeedbackCount() :null)
                    .lessonBall(allByLesson != null ? allByLesson.getFeedbackBall() : null)
                    .quizCount(allByQuiz != null ? allByQuiz.getFeedbackCount() : null)
                    .quizBall(allByQuiz != null ? allByQuiz.getFeedbackBall() : null)
                    .teacherCount(allByTeacher != null ? allByTeacher.getFeedbackCount() : null)
                    .teacherBall(allByTeacher != null ? allByTeacher.getFeedbackBall() : null)
                    .build();
            resFeedbacks.add(resFeedback);
        }

        int totalElements = resFeedbacks.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        List<ResFeedback> pagedList = resFeedbacks.subList(start, end);


        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(totalPages)
                .totalElements(totalElements)
                .body(pagedList)
                .build();

        return new ApiResponse(resPageable);
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
