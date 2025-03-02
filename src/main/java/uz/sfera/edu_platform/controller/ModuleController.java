package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ModuleRequest;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.ModuleService;
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
            @Valid @RequestBody ModuleRequest moduleRequest
    ){
        return ResponseEntity.ok(moduleService.createModule(moduleRequest));
    }

    @GetMapping("/get")
    @Operation(summary = "(TEACHER/ADMIN) name bo'yicha module qidirish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> searchModule(
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(moduleService.searchModule(name, page, size));
    }

    @GetMapping("/{moduleId}")
    @Operation(summary = "(TEACHER/ADMIN/CEO/STUDENT) id bo'yicha modulni olish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN' , 'ROLE_CEO', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(moduleService.getModule(moduleId));
    }

    @GetMapping("getByCategory")
    @Operation(summary = "Categorydagi modullarni ko'rish")
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
            @Valid @RequestBody ModuleRequest moduleRequest
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



    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "STUDENT uziga ochilgan modullarni kurish")
    @GetMapping("/openModules")
    public ResponseEntity<ApiResponse> getOpenModules(@CurrentUser User user){
        ApiResponse openModuleByStudent = moduleService.getOpenModuleByStudent(user);
        return ResponseEntity.ok(openModuleByStudent);
    }
}
