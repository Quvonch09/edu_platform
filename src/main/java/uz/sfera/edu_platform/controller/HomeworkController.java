package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqHomework;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.HomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/homework")
public class HomeworkController {
    private final HomeworkService homeworkService;

    @PostMapping("/create")
    @Operation(summary = "(STUDENT) homework saqlaydi")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> saveHomework(
            @RequestBody ReqHomework reqHomework,
            @CurrentUser User student
            ){
        return ResponseEntity.ok(homeworkService.createHomework(student,reqHomework));
    }

    @PutMapping("/checkHomework/{homeworkId}")
    @Operation(summary = "(Teacher) homeworklarni tekshirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> checkHomework(
            @PathVariable Long homeworkId,
            @RequestParam byte ball
    ){
        return ResponseEntity.ok(homeworkService.checkHomework(homeworkId, ball));
    }

    @GetMapping("/myHomeworks")
    @Operation(summary = "Student o'z homeworklarini ko'rish")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> getMyHomeworks(
            @CurrentUser User student,
            @RequestParam boolean isChecked,
            @RequestParam Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(homeworkService.getMyHomeworks(isChecked, student, taskId,page,size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "(TEACHER,ADMIN) Homeworklarni ko'rish",description = "agar byStudent = true bo'lsa studentga bog'liq homeworklar aks holda taskga bog'liq")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getHomework(
            @RequestParam boolean isChecked,
            @PathVariable Long id,
            @RequestParam boolean byStudent,
            @RequestParam(value = "page" , defaultValue = "0") int page,
            @RequestParam(value = "size" , defaultValue = "10") int size
    ){
        return ResponseEntity.ok(homeworkService.getHomeworks(isChecked, id, byStudent , page, size));
    }

    @GetMapping("/myStatistics")
    @Operation(summary = "(STUDENT) O'z natijalarini ko'rish")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> getStatistics(
            @CurrentUser User student
    ){
        return ResponseEntity.ok(homeworkService.userStatistics(student));
    }

}
