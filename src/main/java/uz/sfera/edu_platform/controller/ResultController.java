package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/getByUser/{userId}")
    @Operation(summary = "(TEACHER,ADMIN) Userga tegishli resultlar")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getUserResults(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(resultService.getUserResults(userId, page, size));
    }

    @GetMapping("/myResults")
    @Operation(summary = "User o'zining natijalar tarixi")
    public ResponseEntity<ApiResponse> getUserResultHistory(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(resultService.getUserResults(user.getId(), page, size));
    }

    @GetMapping("/{resultId}")
    @Operation(summary = "Id bo'yicha resultni ko'rish")
    public ResponseEntity<ApiResponse> getResultById(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.getResultById(resultId));
    }

    @DeleteMapping("/delete/{resultId}")
    @Operation(summary = "(TEACHER,ADMIN) resultni o'chirish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteResult(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.deleteResult(resultId));
    }

//    @GetMapping("/getExamStats/{groupId}")
//    public ResponseEntity<ApiResponse> getExamResults(
//            @PathVariable Long groupId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ){
//        return ResponseEntity.ok(resultService.getGroupResults(groupId, page, size));
//    }
}
