package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.entity.enums.PaymentStatusEnum;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqPayment;
import uz.sfera.edu_platform.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;


    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = "CEO paymnet saqlash uchun", description = "Agar payType TUSHUM bulsa paymentStatus shartmas")
    @PostMapping
    public ResponseEntity<ApiResponse> saveRipPayment(
            @RequestParam(required = false, value = "status")PaymentStatusEnum paymentStatus,
            @RequestParam PaymentEnum payType,
            @RequestBody ReqPayment reqPayment
    ){
        ApiResponse ripPayment = paymentService.createRipPayment(paymentStatus, payType, reqPayment);
        return ResponseEntity.ok(ripPayment);
    }


    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = "CEO tulov qilgan/qilmagan uquvchilar sonini kurish")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getPaymentCount(){
        return ResponseEntity.ok(paymentService.getPaymentCount());
    }



    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = "CEO paymentni search qilish")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchPayment(
            @RequestParam(required = false, value = "fullName")String fullName,
            @RequestParam(value = "status" , required = false) PaymentStatusEnum statusEnum,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        ApiResponse search = paymentService.search(fullName, statusEnum, page, size);
        return ResponseEntity.ok(search);
    }


    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = "CEO paymentni update qilish")
    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> updatePayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false, value = "status")PaymentStatusEnum paymentStatus,
            @RequestParam PaymentEnum payType,
            @RequestBody ReqPayment reqPayment
    ){
        return ResponseEntity.ok(paymentService.updatePayment(paymentId, paymentStatus, payType, reqPayment));
    }



    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = "CEO paymentni delete qilish")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> deletePayment(
            @PathVariable Long paymentId
    ){
        ApiResponse apiResponse = paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_CEO')")
    @Operation(summary = " CEO ga oylik chart uchun tushum , chiqim , daromad")
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse> getPaymentChart()
    {
        return ResponseEntity.ok(paymentService.getStatistic());
    }
}
