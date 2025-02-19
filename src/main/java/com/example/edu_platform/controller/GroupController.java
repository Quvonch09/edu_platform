package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqGroup;
import com.example.edu_platform.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN group qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveGroup(@RequestBody ReqGroup reqGroup){
        ApiResponse apiResponse = groupService.saveGroup(reqGroup);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN/CEO/TEACHER groupni search qilish")
    @GetMapping
    public ResponseEntity<ApiResponse> searchGroup(@RequestParam(required = false, value = "name") String name,
                                                   @RequestParam(required = false, value = "teacherName") String teacherName,
                                                   @RequestParam(required = false, value = "startDate") LocalDate startDate,
                                                   @RequestParam(required = false, value = "endDate") LocalDate endDate,
                                                   @RequestParam(required = false, value = "categoryId") Long categoryId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        ApiResponse search = groupService.search(name, teacherName, startDate, endDate, categoryId, page, size);
        return ResponseEntity.ok(search);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER/CEO bitta gorupni kurish")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> getGroup(@PathVariable Long groupId){
        ApiResponse oneGroup = groupService.getOneGroup(groupId);
        return ResponseEntity.ok(oneGroup);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN groupni update qilish")
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse> updateGroup(@PathVariable Long groupId, @RequestBody ReqGroup reqGroup){
        ApiResponse apiResponse = groupService.updateGroup(groupId, reqGroup);
        return ResponseEntity.ok(apiResponse);
    }




    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN groupni delete qilish")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse> deleteGroup(@PathVariable Long groupId){
        ApiResponse apiResponse = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(apiResponse);
    }
}
