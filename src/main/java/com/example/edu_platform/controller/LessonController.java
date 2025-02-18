package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.LessonRequest;
import com.example.edu_platform.payload.req.ReqLessonTracking;
import com.example.edu_platform.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lesson")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/create-lesson")
    @Operation(summary = "O'qituvchi dars yaratish")
//    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> createLesson(
            @RequestBody LessonRequest lessonRequest
    ){
        return ResponseEntity.ok(lessonService.createLesson(lessonRequest));
    }

    @GetMapping("/lesson-in-module/{moduleId}")
    @Operation(summary = "Moduldagi darslarni ko'rish")
//    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getLessons(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(lessonService.getLessonInModule(moduleId));
    }

    @PutMapping("/update-lesson/{lessonId}")
    @Operation(summary = "Darsni tahrirlash o'qituvchi uchun")
//    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long lessonId,
            @RequestBody LessonRequest lessonRequest
    ){
        return ResponseEntity.ok(lessonService.update(lessonId,lessonRequest));
    }

    @DeleteMapping("/delete/{lessonId}")
    @Operation(summary = "Darsni o'chirish")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long lessonId
    ){
        return ResponseEntity.ok(lessonService.delete(lessonId));
    }

    @PostMapping("/allow-lesson")
    @Operation(summary = "Darsga ruxsat berish")
//    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> allowLesson(
            @RequestBody ReqLessonTracking reqLessonTracking
            ){
        return ResponseEntity.ok(lessonService.allowLesson(reqLessonTracking));
    }

    @GetMapping("/open-lessons-for-group/{groupId}")
    @Operation(summary = "Guruhdagi ochiq darslar")
    public ResponseEntity<ApiResponse> openLessons(
            @PathVariable Long groupId
    ){
        return ResponseEntity.ok(lessonService.getOpenLessonsInGroup(groupId));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Dars statistikasini ko'rish")
//    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getStatistics(){
        return ResponseEntity.ok(lessonService.getStatistics());
    }
}
