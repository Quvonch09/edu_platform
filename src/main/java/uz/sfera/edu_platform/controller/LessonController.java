package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.LessonRequest;
import uz.sfera.edu_platform.payload.req.ReqLessonFiles;
import uz.sfera.edu_platform.payload.req.ReqLessonTracking;
import uz.sfera.edu_platform.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/create")
    @Operation(summary = "O'qituvchi dars yaratish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> createLesson(
            @RequestBody LessonRequest lessonRequest
    ){
        return ResponseEntity.ok(lessonService.createLesson(lessonRequest));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> search(
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(lessonService.search(name, size, page));
    }

    @GetMapping("/getByModule/{moduleId}")
    @Operation(summary = "(TEACHER,ADMIN,CEO) Moduldagi darslarni ko'rish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_CEO')")
    public ResponseEntity<ApiResponse> getLessons(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(lessonService.getLessonInModule(moduleId));
    }

    @PutMapping("/update/{lessonId}")
    @Operation(summary = "(TEACHER) Darsni tahrirlash o'qituvchi uchun")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long lessonId,
            @RequestBody LessonRequest lessonRequest
    ){
        return ResponseEntity.ok(lessonService.update(lessonId,lessonRequest));
    }

    @DeleteMapping("/delete/{lessonId}")
    @Operation(summary = "(TEACHER) Darsni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long lessonId
    ){
        return ResponseEntity.ok(lessonService.delete(lessonId));
    }

    @PostMapping("/allowLesson")
    @Operation(summary = "(TEACHER) Darsga ruxsat berish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> allowLesson(
            @RequestBody ReqLessonTracking reqLessonTracking
            ){
        return ResponseEntity.ok(lessonService.allowLesson(reqLessonTracking));
    }

    @GetMapping("/getOpenByGroup/{groupId}")
    @Operation(summary = "Guruhdagi ochiq darslarni ko'rish")
    public ResponseEntity<ApiResponse> openLessons(
            @PathVariable Long groupId
    ){
        return ResponseEntity.ok(lessonService.getOpenLessonsInGroup(groupId));
    }

    @PostMapping("/addFiles")
    @Operation(summary = "(TEACHER) Dars uchun fayllar biriktirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> addFile(
            @RequestBody ReqLessonFiles reqLessonFiles
    ){
        return ResponseEntity.ok(lessonService.addFile(reqLessonFiles));
    }

    @DeleteMapping("/deleteFiles")
    @Operation(summary = "(TEACHER) Dars ichidagi fayllarni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> removeFile(
            @RequestBody ReqLessonFiles reqLessonFiles
    ){
        return ResponseEntity.ok(lessonService.deleteFiles(reqLessonFiles));
    }

    @PutMapping("/updateFiles")
    @Operation(summary = "(TEACHER) Fayllarni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateFiles(
            @RequestBody ReqLessonFiles reqLessonFiles,
            @RequestParam List<Long> oldFiles
    ){
        return ResponseEntity.ok(lessonService.updateFiles(reqLessonFiles, oldFiles));
    }

    @GetMapping("/statistics")
    @Operation(summary = "(TEACHER,ADMIN,CEO) Dars statistikasini ko'rish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_CEO')")
    public ResponseEntity<ApiResponse> getStatistics(){
        return ResponseEntity.ok(lessonService.getStatistics());
    }
}
