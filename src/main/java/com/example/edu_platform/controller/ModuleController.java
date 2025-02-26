package com.example.edu_platform.controller;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ModuleRequest;
import com.example.edu_platform.security.CurrentUser;
import com.example.edu_platform.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/module")
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping("/create")
    @Operation(summary = "(TEACHER/ADMIN) Modul qo'shish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> create(
            @RequestBody ModuleRequest moduleRequest
    ){
        return ResponseEntity.ok(moduleService.createModule(moduleRequest));
    }

    @GetMapping("/get")
    @Operation(summary = "(TEACHER/ADMIN) name bo'yicha module qidirish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> searchModule(
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(moduleService.searchModule(name, page, size));
    }

    @GetMapping("/{moduleId}")
    @Operation(summary = "(TEACHER/ADMIN/CEO) id bo'yicha modulni olish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN' , 'ROLE_CEO', 'ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(moduleService.getModule(moduleId));
    }

    @GetMapping("getByCategory/{categoryId}")
    @Operation(summary = "(TEACHER/ADMIN/CEO) Categorydagi modullarni ko'rish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN','ROLE_CEO', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> getByCategory(
            @RequestParam(required = false) Long categoryId,
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(moduleService.getByCategory(categoryId, user, page, size));
    }

    @PutMapping("/update/{moduleId}")
    @Operation(summary = "(TEACHER/ADMIN) Modulni yangilash")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long moduleId,
            @RequestBody ModuleRequest moduleRequest
    ){
        return ResponseEntity.ok(moduleService.update(moduleId, moduleRequest));
    }

    @DeleteMapping("/delete/{moduleId}")
    @Operation(summary = "(TEACHER/ADMIN) Modulni o'chirish")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(moduleService.delete(moduleId));
    }
}
