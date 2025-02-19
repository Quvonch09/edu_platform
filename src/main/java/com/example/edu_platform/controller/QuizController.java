package com.example.edu_platform.controller;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqPassTest;
import com.example.edu_platform.payload.req.ReqQuiz;
import com.example.edu_platform.security.CurrentUser;
import com.example.edu_platform.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    @Operation(summary = "Quiz yaratish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> createQuiz(@RequestBody ReqQuiz reqQuiz) {
        return ResponseEntity.ok(quizService.createQuiz(reqQuiz));
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Id bo'yicha quiz olish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Lesson bo'yicha quizlarni olish")
    public ResponseEntity<ApiResponse> getByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(quizService.getQuizByLesson(lessonId));
    }

    @PutMapping("/{quizId}")
    @Operation(summary = "Quizni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateQuiz(@PathVariable Long quizId, @RequestBody ReqQuiz reqQuiz) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, reqQuiz));
    }

    @DeleteMapping("/{quizId}")
    @Operation(summary = "Quizni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.deleteQuiz(quizId));
    }

    @GetMapping("/startTest/{quizId}")
    @Operation(summary = "Testni boshlash")
    public ResponseEntity<ApiResponse> startTest(
            @PathVariable Long quizId,
            @CurrentUser User user
    ){
        return ResponseEntity.ok(quizService.startTest(user, quizId));
    }

    @PostMapping("/{quizId}/pass-test")
    @Operation(summary = "Testni topshirish")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> passTest(
            @PathVariable Long quizId,
            @RequestBody List<ReqPassTest> passTestList,
            @CurrentUser User user,
            @RequestParam Long timeTaken) {
        return ResponseEntity.ok(quizService.passTest(passTestList, user,quizId));
    }
}
