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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER student qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveStudent(@RequestBody ReqStudent reqStudent){
        ApiResponse apiResponse = studentService.saveStudent(reqStudent);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO','ROLE_TEACHER')")
    @Operation(summary = "Student search")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> getAllStudents(@RequestParam(required = false) String fullName,
                                                      @RequestParam(required = false) String phoneNumber,
                                                      @RequestParam(value = "status" , required = false) UserStatus userStatus,
                                                      @RequestParam(required = false) String groupName,
                                                      @RequestParam(required = false) Long teacherId,
                                                      @RequestParam(required = false) Integer startAge,
                                                      @RequestParam(required = false) Integer endAge,
                                                      @RequestParam(required = false) Boolean hasPaid,
                                                      @RequestParam(  value = "page", defaultValue = "0") int page,
                                                      @RequestParam( value = "size",defaultValue = "10") int size){
        ApiResponse apiResponse = studentService.searchStudent(fullName, phoneNumber, userStatus, groupName, teacherId,
                startAge, endAge,hasPaid, page, size);
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


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_CEO')")
    @Operation(summary = "Admin/Ceo/Teacher studentni group buyicha listi")
    @GetMapping("/groupBy/{groupId}")
    public ResponseEntity<ApiResponse> getStudent(@PathVariable Long groupId){
        ApiResponse apiResponse = studentService.getStudentGroupBy(groupId);
        return ResponseEntity.ok(apiResponse);
    }


}
