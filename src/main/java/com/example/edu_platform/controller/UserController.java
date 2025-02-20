package com.example.edu_platform.controller;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.UserDTO;
import com.example.edu_platform.payload.req.ReqAdmin;
import com.example.edu_platform.payload.req.ReqTeacher;
import com.example.edu_platform.security.CurrentUser;
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


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher qushish")
    @PostMapping("/saveTeacher")
    public ResponseEntity<ApiResponse> saveTeacher(@RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.saveTeacher(reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN' , 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher va adminlarni search qilish")
    @GetMapping("/searchUsers")
    public ResponseEntity<ApiResponse> searchUsers(@RequestParam(required = false, value = "fullName") String fullName,
                                                     @RequestParam(required = false, value = "phoneNumber") String phoneNumber,
                                                     @RequestParam(required = false, value = "groupId") Long groupId,
                                                     @RequestParam(value = "role") Role role,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        ApiResponse apiResponse = userService.searchUsers(fullName, phoneNumber, groupId,role, page, size);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni bittasini kurish")
    @GetMapping("/teacherById/{teacherId}")
    public ResponseEntity<ApiResponse> getOneTeacher(@PathVariable Long teacherId) {
        ApiResponse apiResponse = userService.getOneTeacher(teacherId);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni update qilish")
    @PutMapping("/updateTeacher/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacher(@PathVariable Long teacherId, @RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.updateTeacher(teacherId, reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni activeni uzgartirish")
    @PutMapping("/updateTeacher/active/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacherActive(@PathVariable Long teacherId, @RequestParam Boolean active) {
        ApiResponse apiResponse = userService.updateActiveTeacher(teacherId, active);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher/admin delete qilish")
    @DeleteMapping("/deleteTeacher/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId, @CurrentUser User user) {
        ApiResponse apiResponse = userService.deleteTeacher(userId, user);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO admin qushish")
    @PostMapping("/saveAdmin")
    public ResponseEntity<ApiResponse> saveAdmin(@RequestBody ReqAdmin reqAdmin) {
        ApiResponse apiResponse = userService.saveAdmin(reqAdmin);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO admin bittasini kurish")
    @GetMapping("/adminById/{adminId}")
    public ResponseEntity<ApiResponse> getOneAdmin(@PathVariable Long adminId) {
        ApiResponse apiResponse = userService.getOneAdmin(adminId);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni update qilish")
    @PutMapping("/updateAdmin/{adminId}")
    public ResponseEntity<ApiResponse> updateAdmin(@PathVariable Long adminId, @RequestBody ReqAdmin reqAdmin) {
        ApiResponse apiResponse = userService.updateAdmin(adminId, reqAdmin);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Barcha uzini profilini kurish")
    @GetMapping("/getMe")
    public ResponseEntity<ApiResponse> getMe(@CurrentUser User user) {
        ApiResponse apiResponse = userService.getMe(user);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Barcha user uzini profilini update qilish")
    @PutMapping("/updateUser")
    public ResponseEntity<ApiResponse> updateUser(@CurrentUser User user, @RequestBody UserDTO userDTO) {
        ApiResponse apiResponse = userService.updateUser(user, userDTO);
        return ResponseEntity.ok(apiResponse);
    }

}
