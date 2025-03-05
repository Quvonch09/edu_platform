package uz.sfera.edu_platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.FeedbackDto;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/leave/toTeacher")
    public ResponseEntity<ApiResponse> leaveFeedbackToTeacher(@RequestBody FeedbackDto feedback,
                                                              @CurrentUser User user) {
        ApiResponse apiResponse = feedbackService.leaveFeedbackToTeacher(feedback, user);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/leave/toLesson")
    public ResponseEntity<ApiResponse> leaveFeedbackToLesson(@RequestBody FeedbackDto feedback,
                                                             @CurrentUser User user) {
        ApiResponse apiResponse = feedbackService.leaveFeedbackToLesson(feedback, user);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/leave/toQuiz")
    public ResponseEntity<ApiResponse> leaveFeedbackToQuiz(@RequestBody FeedbackDto feedback,
                                                           @CurrentUser User user) {
        ApiResponse apiResponse = feedbackService.leaveFeedbackToQuiz(feedback, user);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<ApiResponse> updateFeedback(@PathVariable("feedbackId") Long feedbackId,
                                                      @RequestParam String feedback,
                                                      @RequestParam int rating,
                                                      @CurrentUser User user) {
        ApiResponse apiResponse = feedbackService.editFeedback(feedback, rating, feedbackId, user);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getByTeacherId")
    public ResponseEntity<ApiResponse> getFeedbackByTeacherId(@RequestParam Long teacherId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        ApiResponse feedbacks = feedbackService.getAllByTeacherId(teacherId, page, size);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/getByStudentId")
    public ResponseEntity<ApiResponse> getFeedbackByStudentId(@RequestParam Long studentId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        ApiResponse feedbacks = feedbackService.getAllByUserId(studentId, page, size);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/getByLessonId")
    public ResponseEntity<ApiResponse> getFeedbackByLessonId(@RequestParam Long lessonId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        ApiResponse feedbacks = feedbackService.getAllByLessonId(lessonId, page, size);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/getByQuizId")
    public ResponseEntity<ApiResponse> getFeedbackByQuizId(@RequestParam Long quizId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        ApiResponse feedbacks = feedbackService.getAllByQuizId(quizId, page, size);
        return ResponseEntity.ok(feedbacks);
    }
}
