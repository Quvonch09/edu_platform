package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqGroup;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.GroupService;
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
    public ResponseEntity<ApiResponse> saveGroup(@Valid @RequestBody ReqGroup reqGroup){
        ApiResponse apiResponse = groupService.saveGroup(reqGroup);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getByTeacher")
    @Operation(summary = "Teacher o'ziga tegishli guruhlarni ko'rish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> getByTeacher(
            @CurrentUser User teacher
    ){
        return ResponseEntity.ok(groupService.getGroupByTeacher(teacher));
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN/CEO/TEACHER groupni search qilish")
    @GetMapping
    public ResponseEntity<ApiResponse> searchGroup(
            @CurrentUser User teacher,
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(required = false, value = "teacherName") String teacherName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, value = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        ApiResponse search = groupService.search(teacher,name, teacherName, startDate, endDate, categoryId, page, size);
        return ResponseEntity.ok(search);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER/CEO bitta groupni kurish")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> getGroup(
            @PathVariable Long groupId
    ){
        ApiResponse oneGroup = groupService.getOneGroup(groupId);
        return ResponseEntity.ok(oneGroup);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER/CEO guruhlar listini kurish")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getGroupList(@CurrentUser User user){
        ApiResponse oneGroup = groupService.getGroupsList(user);
        return ResponseEntity.ok(oneGroup);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CEO')")
    @Operation(summary = "ADMIN groupni update qilish")
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody ReqGroup reqGroup
    ){
        ApiResponse apiResponse = groupService.updateGroup(groupId, reqGroup);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/redirect")
    @Operation(summary = "Admin Guruhning hamma studentlarini boshqa guruhga o'tkazish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> redirectGroup(
            @RequestParam Long groupId,
            @RequestParam Long targetGroupId
    ){
        ApiResponse apiResponse = groupService.redirectGroupStudents(groupId, targetGroupId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CEO')")
    @Operation(summary = "ADMIN groupni vaqtini uzgartirish uchun")
    @PutMapping("/updateGroup/EndDate")
    public ResponseEntity<ApiResponse> updateGroupEndDate(
            @RequestParam Long groupId,
            @RequestParam int duration
    ){
        ApiResponse apiResponse = groupService.updateEndDateGroup(groupId, duration);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN groupni delete qilish")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse> deleteGroup(
            @PathVariable Long groupId
    ){
        ApiResponse apiResponse = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(apiResponse);
    }
}
