package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqIncome;
import uz.sfera.edu_platform.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Month;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @Operation(summary = "Admin/ceo tushum saqlash uchun")
    @PostMapping
    public ResponseEntity<ApiResponse> saveRipPayment(
            @Valid @RequestBody ReqIncome reqIncome
    ){
        ApiResponse ripPayment = incomeService.createRipPayment(reqIncome);
        return ResponseEntity.ok(ripPayment);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @Operation(summary = "CEO/admin tulov qilgan/qilmagan uquvchilar sonini kurish")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getPaymentCount(
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Month month,
            @RequestParam(required = false) boolean paid
    ){
        return ResponseEntity.ok(incomeService.getIncomeCount(studentName, paid, month));
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN')")
    @Operation(summary = "CEO/admin paymentni search qilish")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchPayment(
            @RequestParam(required = false, value = "studentName")String fullName,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "month", required = false) Month month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        ApiResponse search = incomeService.search(fullName,paid,month,page,size);
        return ResponseEntity.ok(search);
    }
}

