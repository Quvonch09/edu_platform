package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.service.QuizSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-settings")
public class QuizSettingsController {
    private final QuizSettingsService quizSettingsService;

//    @PutMapping("/update/{settingId}")
//    @Operation(summary = "(TEACHER) parametrlarni yangilash")
//    @PreAuthorize("hasRole('ROLE_TEACHER')")
//    public ResponseEntity<ApiResponse> updateSettings(
//            @PathVariable Long settingId,
//            @RequestBody ReqQuizSettings reqQuizSettings
//    ){
//        return ResponseEntity.ok(quizSettingsService.updateSettings(settingId, reqQuizSettings));
//    }

    @GetMapping("/getByQuiz/{quizId}")
    @Operation(summary = "Parametrlarni ko'rish quiz bo'yicha")
    public ResponseEntity<ApiResponse> getSettings(
            @PathVariable Long quizId
    ){
        return ResponseEntity.ok(quizSettingsService.getSettings(quizId));
    }
}
