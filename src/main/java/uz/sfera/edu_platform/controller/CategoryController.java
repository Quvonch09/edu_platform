package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.CategoryDTO;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole( 'ROLE_CEO')")
    @Operation(summary = "CEO category qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> addCategory(
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
        ApiResponse apiResponse = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN','ROLE_TEACHER','ROLE_STUDENT')")
    @Operation(summary = "Barcha categorylarni kurish")
    @GetMapping
    public ResponseEntity<ApiResponse> search(
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false, value = "description") String description,
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        ApiResponse allCategories = categoryService.getAllCategories(user,name,teacherId, description, page, size);
        return ResponseEntity.ok(allCategories);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN','ROLE_TEACHER','ROLE_STUDENT')")
    @Operation(summary = "Barcha categoryni bittasini kurish")
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> getCategory(
            @PathVariable Long categoryId
    ){
        ApiResponse categoryById = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryById);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN','ROLE_TEACHER','ROLE_STUDENT')")
    @Operation(summary = "Barcha categoryni listini kurish")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getCategoryList(@CurrentUser User user) {
        ApiResponse categoryById = categoryService.getAllList(user);
        return ResponseEntity.ok(categoryById);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO categoryni update qilish")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
        ApiResponse apiResponse = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO categoryni activeni update qilish")
    @PutMapping("/updateActive/{categoryId}")
    public ResponseEntity<ApiResponse> updateActiveCategory(
            @PathVariable Long categoryId,
            @RequestParam boolean active
    ){
        ApiResponse apiResponse = categoryService.updateActiveCategory(categoryId, active);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO categoryni delete qilish")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long categoryId
    ){
        ApiResponse apiResponse = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(apiResponse);
    }
}
