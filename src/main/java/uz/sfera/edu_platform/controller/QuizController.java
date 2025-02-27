package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqPassTest;
import uz.sfera.edu_platform.payload.req.ReqQuiz;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.QuizService;
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

    @PostMapping("/create")
    @Operation(summary = "(TEACHER) Quiz yaratish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> createQuiz(
            @RequestBody ReqQuiz reqQuiz
    ) {
        return ResponseEntity.ok(quizService.createQuiz(reqQuiz));
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "(TEACHER,ADMIN) Id bo'yicha quiz olish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @GetMapping("/getByLesson/{lessonId}")
    @Operation(summary = "(TEACHER,ADMIN) Lesson bo'yicha quizlarni olish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getByLesson(
            @PathVariable Long lessonId
    ) {
        return ResponseEntity.ok(quizService.getQuizByLesson(lessonId));
    }

    @PutMapping("/update/{quizId}")
    @Operation(summary = "(TEACHER) Quizni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody ReqQuiz reqQuiz
    ) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, reqQuiz));
    }

    @DeleteMapping("/{quizId}")
    @Operation(summary = "(TEACHER) Quizni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteQuiz(
            @PathVariable Long quizId
    ) {
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

    @PostMapping("/passTest/{quizId}")
    @Operation(summary = "(STUDENT) Testni topshirish")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> passTest(
            @PathVariable Long quizId,
            @RequestBody List<ReqPassTest> passTestList,
            @CurrentUser User user,
            @RequestParam Long timeTaken) {
        return ResponseEntity.ok(quizService.passTest(passTestList, user,quizId));
    }
}
