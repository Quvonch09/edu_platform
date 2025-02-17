package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqTeacher;
import com.example.edu_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher qushish")
    @PostMapping("/saveTeacher")
    public ResponseEntity<ApiResponse> saveTeacher(@RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.saveTeacher(reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni search qilish")
    @GetMapping("/searchTeacher")
    public ResponseEntity<ApiResponse> searchTeacher(@RequestParam(required = false, value = "fullName") String fullName,
                                                     @RequestParam(required = false, value = "phoneNumber") String phoneNumber,
                                                     @RequestParam(required = false, value = "groupId") Long groupId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        ApiResponse apiResponse = userService.searchTeacher(fullName, phoneNumber, groupId, page, size);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni bittasini kurish")
    @GetMapping("/teacherById/{teacherId}")
    public ResponseEntity<ApiResponse> getOneTeacher(@PathVariable Long teacherId) {
        ApiResponse apiResponse = userService.getOneTeacher(teacherId);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni update qilish")
    @PutMapping("/updateTeacher/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacher(@PathVariable Long teacherId, @RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.updateTeacher(teacherId, reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni activeni uzgartirish")
    @PutMapping("/updateTeacher/active/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacherActive(@PathVariable Long teacherId, @RequestParam Boolean active) {
        ApiResponse apiResponse = userService.updateActiveTeacher(teacherId, active);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni delete qilish")
    @DeleteMapping("/deleteTeacher/{teacherId}")
    public ResponseEntity<ApiResponse> deleteTeacher(@PathVariable Long teacherId) {
        ApiResponse apiResponse = userService.deleteTeacher(teacherId);
        return ResponseEntity.ok(apiResponse);
    }
}
