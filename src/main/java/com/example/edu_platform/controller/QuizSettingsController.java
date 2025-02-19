package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqQuizSettings;
import com.example.edu_platform.service.QuizSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-settings")
public class QuizSettingsController {
    private final QuizSettingsService quizSettingsService;

    @PutMapping("/update/{settingId}")
    @Operation(summary = "parametrlarni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateSettings(
            @PathVariable Long settingId,
            @RequestBody ReqQuizSettings reqQuizSettings
    ){
        return ResponseEntity.ok(quizSettingsService.updateSettings(settingId, reqQuizSettings));
    }

    @GetMapping("/get/{quizId}")
    @Operation(summary = "Parametrlarni ko'rish quiz bo'yicha")
    public ResponseEntity<ApiResponse> getSettings(
            @PathVariable Long quizId
    ){
        return ResponseEntity.ok(quizSettingsService.getSettings(quizId));
    }
}
