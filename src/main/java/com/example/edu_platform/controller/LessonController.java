package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.LessonRequest;
import com.example.edu_platform.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lesson")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/create-lesson")
    public ResponseEntity<ApiResponse> createLesson(
            @RequestBody LessonRequest lessonRequest
            ){
        return ResponseEntity.ok(lessonService.createLesson(lessonRequest));
    }

    @GetMapping("/lesson-in-module/{moduleId}")
    public ResponseEntity<ApiResponse> getLessons(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(lessonService.getLessonInModule(moduleId));
    }

    @PutMapping("/update-lesson/{lessonId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long lessonId,
            @RequestBody LessonRequest lessonRequest
    ){
        return ResponseEntity.ok(lessonService.update(lessonId,lessonRequest));
    }

    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long lessonId
    ){
        return ResponseEntity.ok(lessonService.delete(lessonId));
    }

    @PostMapping("/allow-lesson")
    public ResponseEntity<ApiResponse> allowLesson(
            @RequestParam Long lessonId,
            @RequestParam Long groupId
    ){
        return ResponseEntity.ok(lessonService.allowLesson(lessonId, groupId));
    }

    @GetMapping("/open-lessons-for-group/{groupId}")
    public ResponseEntity<ApiResponse> openLessons(
            @PathVariable Long groupId
    ){
        return ResponseEntity.ok(lessonService.getOpenLessonsInGroup(groupId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getStatistics(){
        return ResponseEntity.ok(lessonService.getStatistics());
    }
}
