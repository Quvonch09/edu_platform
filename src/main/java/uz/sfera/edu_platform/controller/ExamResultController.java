package uz.sfera.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ExamResultRequest;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.ExamResultService;

import java.time.Month;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam")
public class ExamResultController {
    private final ExamResultService examResultService;

    @PostMapping("/addResult")
    @Operation(summary = "(TEACHER) Imtihon natijasini qo'shish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> addExamResult(
            @Valid @RequestBody ExamResultRequest examResultRequest,
            @RequestParam Month month
    ){
        return ResponseEntity.ok(examResultService.createExamResult(month, examResultRequest));
    }

    @GetMapping("/get")
    @Operation(summary = "(TEACHER) Filter orqali imtihon natijalarini olish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> getExams(
            @CurrentUser User teacher,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Month month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(examResultService.getAll(teacher,month, studentId, page, size));
    }

    @GetMapping("/myExamResults")
    @Operation(summary = "Student o'z natijalarini ko'rish")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> getOwnExams(
            @CurrentUser User student,
            @RequestParam(required = false) Month month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(examResultService.getAllStudent(student, month, page, size));
    }
}
