package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.entity.enums.QuestionEnum;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqQuestion;
import uz.sfera.edu_platform.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/save")
    @Operation(summary = "(TEACHER) Savol qo'shish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> saveQuestion(
            @RequestParam QuestionEnum questionEnum,
            @RequestBody ReqQuestion reqQuestion
    ){
        return ResponseEntity.ok(questionService.saveQuestion(questionEnum, reqQuestion));
    }

    @GetMapping("/getByQuiz/{quizId}")
    @Operation(summary = "Quizga tegishli questionlist ni olish")
    public ResponseEntity<ApiResponse> getByQuiz(
            @PathVariable Long quizId
    ){
        return ResponseEntity.ok(questionService.getQuestionByQuiz(quizId));
    }

    @PutMapping("/update/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "(TEACHER) Questionni update qilish")
    public ResponseEntity<ApiResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestParam QuestionEnum difficulty,
            @RequestBody ReqQuestion reqQuestion
    ){
        return ResponseEntity.ok(questionService.updateQuestion(questionId, difficulty, reqQuestion));
    }

    @DeleteMapping("/delete/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "(TEACHER) Question o'chirish")
    public ResponseEntity<ApiResponse> deleteQuestion(
            @PathVariable Long questionId
    ){
        return ResponseEntity.ok(questionService.deleteQuiz(questionId));
    }
}
