package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqTask;
import uz.sfera.edu_platform.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/save-task")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Task qo'shish")
    public ResponseEntity<ApiResponse> saveTask(
            @RequestBody ReqTask reqTask
    ){
        return ResponseEntity.ok(taskService.saveTask(reqTask));
    }

    @GetMapping("/get/{taskId}")
    @Operation(summary = "id bo'yicha task olish")
    public ResponseEntity<ApiResponse> getTask(
            @PathVariable Long taskId
    ){
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @GetMapping("/get-by-lesson/{lessonId}")
    @Operation(summary = "lesson bo'yicha tasklarni olish")
    public ResponseEntity<ApiResponse> getTaskByLesson(
            @PathVariable Long lessonId
    ){
        return ResponseEntity.ok(taskService.getTaskInLesson(lessonId));
    }

    @PutMapping("/update/{taskId}")
    @Operation(summary = "Taskni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody ReqTask reqTask
    ){
        return ResponseEntity.ok(taskService.updateTask(taskId, reqTask));
    }

    @DeleteMapping("/delete/{taskId}")
    @Operation(summary = "Taskni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Long taskId
    ){
        return ResponseEntity.ok(taskService.delete(taskId));
    }
}
