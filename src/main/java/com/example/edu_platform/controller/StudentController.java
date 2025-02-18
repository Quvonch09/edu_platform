package com.example.edu_platform.controller;

import com.example.edu_platform.entity.enums.UserStatus;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqStudent;
import com.example.edu_platform.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "ADMIN/TEACHER student qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveStudent(@RequestBody ReqStudent reqStudent){
        ApiResponse apiResponse = studentService.saveStudent(reqStudent);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Student search")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllStudents(@RequestParam(required = false, value = "fullName") String fullName,
                                                      @RequestParam(required = false, value = "phoneNumber") String phoneNumber,
                                                      @RequestParam(value = "status") UserStatus userStatus,
                                                      @RequestParam(required = false, value = "groupName") String groupName,
                                                      @RequestParam(required = false, value = "teacherId") Long teacherId,
                                                      @RequestParam(required = false, value = "startAge") Integer startAge,
                                                      @RequestParam(required = false, value = "endAge") Integer endAge,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        ApiResponse apiResponse = studentService.searchStudent(fullName, phoneNumber, userStatus, groupName, teacherId, startAge, endAge, page, size);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_CEO')")
    @Operation(summary = "ADMIN/CEO/TEACHER studentni bittasini kurish")
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse> getOneStudent(@PathVariable Long studentId){
        ApiResponse apiResponse = studentService.getOneStudent(studentId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_CEO','ROLE_STUDENT')")
    @Operation(summary = "Barcha studentni update qilish")
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse> updateStudent(@PathVariable Long studentId, @RequestBody ReqStudent reqStudent){
        ApiResponse apiResponse = studentService.updateStudent(studentId, reqStudent);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_CEO')")
    @Operation(summary = "Admin/Ceo/Teacher studentni delete qilish")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Long studentId,
                                                     @RequestParam(value = "chiqib ketgan sanasi") LocalDate date,
                                                     @RequestParam(value = "nima uchun chiqib ketgan") String description){
        ApiResponse apiResponse = studentService.deleteStudent(studentId,date,description);
        return ResponseEntity.ok(apiResponse);
    }
}
