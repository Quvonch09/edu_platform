package uz.sfera.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.entity.enums.OutcomeStatus;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqOutcome;
import uz.sfera.edu_platform.service.OutcomeService;

import java.time.LocalDate;
import java.time.Month;

@RestController
@RequestMapping("/api/outcome")
@RequiredArgsConstructor
public class OutcomeController {
    private final OutcomeService outcomeService;


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @Operation(summary = "CEO techerlar uchun chiqim qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveOutcome(@RequestParam OutcomeStatus outcomeStatus,
                                                   @RequestBody ReqOutcome reqOutcome) {
        return ResponseEntity.ok(outcomeService.saveOutcome(reqOutcome, outcomeStatus));
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @Operation(summary = "CEO/ADMIN chiqimlarni search qilish")
    @GetMapping
    public ResponseEntity<ApiResponse> getOutcome(@RequestParam(value = "teacherName", required = false) String teacherName,
                                                  @RequestParam(value = "month", required = false) Month month,
                                                  @RequestParam(value = "status", required = false) OutcomeStatus outcomeStatus,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(outcomeService.searchOutcome(teacherName, month, outcomeStatus, page, size));
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN')")
    @Operation(summary = "CEO/ADMIN outcome sonlarini kurish")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getOutcomeCount(@RequestParam(required = false) String teacherName,
                                                       @RequestParam(required = false) Month month,
                                                       @RequestParam(required = false) OutcomeStatus outcomeStatus){
        return ResponseEntity.ok(outcomeService.getCountOutcome(teacherName,month,outcomeStatus));
    }


    @Operation(summary = "CEO chiqimlarni uchirish uchun")
    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteOutcome(@PathVariable Long id) {
        return ResponseEntity.ok(outcomeService.deleteOutcome(id));
    }
}
