package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqQuiz;
import com.example.edu_platform.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {
    private final QuizService quizService;

    @PostMapping("/create")
    @Operation(summary = "Quiz yaratish")
    public ResponseEntity<ApiResponse> createQuiz(
            @RequestBody ReqQuiz reqQuiz
    ){
        return ResponseEntity.ok(quizService.createQuiz(reqQuiz));
    }

    @GetMapping("/get/{quizId}")
    @Operation(summary = "Id bo'yicha quiz olish")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long quizId
    ){
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @GetMapping("/get-by-lesson/{lessonId}")
    @Operation(summary = "Lesson bo'yicha quizlarni olish")
    public ResponseEntity<ApiResponse> getByLesson(
            @PathVariable Long lessonId
    ){
        return ResponseEntity.ok(quizService.getQuizByLesson(lessonId));
    }

    @PutMapping("/update/{quizId}")
    @Operation(summary = "Quizni yangilash")
    public ResponseEntity<ApiResponse> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody ReqQuiz reqQuiz
    ){
        return ResponseEntity.ok(quizService.updateQuiz(quizId, reqQuiz));
    }

    @DeleteMapping("/delete/{quizId}")
    @Operation(summary = "Quizni o'chirish")
    public ResponseEntity<ApiResponse> deleteQuiz(
            @PathVariable Long quizId
    ){
        return ResponseEntity.ok(quizService.deleteQuiz(quizId));
    }
}
