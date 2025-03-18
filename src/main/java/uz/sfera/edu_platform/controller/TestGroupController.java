package uz.sfera.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqStudent;
import uz.sfera.edu_platform.service.TestGroupService;

@RestController
@RequestMapping("/api/test-group")
@RequiredArgsConstructor
public class TestGroupController {
    private final TestGroupService testGroupService;

    @PostMapping("/create")
    @Operation(summary = "ADMIN test guruh yaratish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> create(
            @RequestParam String name
    ){
        return ResponseEntity.ok(testGroupService.createGroup(name));
    }

    @GetMapping("/get")
    @Operation(summary = "CEO/ADMIN test guruhlarni ko'rish va filtrlash")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CEO')")
    public ResponseEntity<ApiResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(testGroupService.getGroups(name, active, page, size));
    }

    @GetMapping("/list")
    @Operation(summary = "Test guruhlar listini ko'rish")
    public ResponseEntity<ApiResponse> getList(){
        return ResponseEntity.ok(testGroupService.getList());
    }

    @GetMapping("/students/{groupId}")
    @Operation(summary = "CEO/ADMIN test guruhdagi studentlarni ko'rish")
    public ResponseEntity<ApiResponse> getStudents(
            @PathVariable Long groupId
    ){
        return ResponseEntity.ok(testGroupService.getStudentsByGroup(groupId));
    }

    @DeleteMapping("/delete/{groupId}")
    @Operation(summary = "ADMIN test guruhni o'chirish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> changeStatus(
            @PathVariable Long groupId
    ){
        return ResponseEntity.ok(testGroupService.delete(groupId));
    }

    @PostMapping("/add-student")
    @Operation(summary = "ADMIN test guruhga o'quvchi qo'shish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> addStudent(
            @RequestBody ReqStudent reqStudent
    ){
        return ResponseEntity.ok(testGroupService.addStudent(reqStudent));
    }

    @PutMapping("/redirect-student")
    @Operation(summary = "ADMIN studentni haqiqiy guruhga o'tkazish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> redirectStudent(
            @RequestParam Long studentId,
            @RequestParam Long realGroupId
    ){
        return ResponseEntity.ok(testGroupService.redirectStudent(studentId, realGroupId));
    }

    @DeleteMapping("/delete-student/{studentId}")
    @Operation(summary = "ADMIN studentni ro'yxatdan o'chirish")
    public ResponseEntity<ApiResponse> deleteStudent(
            @PathVariable Long studentId
    ){
        return ResponseEntity.ok(testGroupService.deleteStudent(studentId));
    }

}
