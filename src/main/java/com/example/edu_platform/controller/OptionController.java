package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqOption;
import com.example.edu_platform.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/option")
public class OptionController {
    private final OptionService optionService;

    @PostMapping("/save")
    @Operation(summary = "Javob saqlash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> saveOption(
            @RequestBody ReqOption reqOption,
            @RequestParam boolean isCorrect
    ){
        return ResponseEntity.ok(optionService.saveOption(isCorrect, reqOption));
    }

    @PutMapping("/update/{optionId}")
    @Operation(summary = "Javobni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateOption(
            @RequestBody ReqOption reqOption,
            @PathVariable Long optionId,
            @RequestParam boolean isCorrect
    ){
        return ResponseEntity.ok(optionService.updateOption(optionId, isCorrect, reqOption));
    }

    @DeleteMapping("/delete/{optionId}")
    @Operation(summary = "Javobni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteOption(
            @PathVariable Long optionId
    ){
        return ResponseEntity.ok(optionService.deleteOption(optionId));
    }
}
