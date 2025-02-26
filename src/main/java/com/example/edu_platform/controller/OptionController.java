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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/option")
public class OptionController {
    private final OptionService optionService;

    @PostMapping("/save/{questionId}")
    @Operation(summary = "(TEACHER) Javob saqlash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> saveOption(
            @RequestBody List<ReqOption> reqOption,
            @PathVariable Long questionId
    ){
        return ResponseEntity.ok(optionService.saveOption(questionId,reqOption));
    }

    @PutMapping("/update/{optionId}")
    @Operation(summary = "(TEACHER) Javobni yangilash")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> updateOption(
            @RequestBody ReqOption reqOption,
            @PathVariable Long optionId
    ){
        return ResponseEntity.ok(optionService.updateOption(optionId, reqOption));
    }

    @GetMapping("/getByQuestion/{questionId}")
    @Operation(summary = "Question bo'yicha optionlarni ko'rish")
    public ResponseEntity<ApiResponse> getByQuestion(
            @PathVariable Long questionId
    ){
        return ResponseEntity.ok(optionService.getByQuestion(questionId));
    }

    @DeleteMapping("/delete/{optionId}")
    @Operation(summary = "(TEACHER) Javobni o'chirish")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse> deleteOption(
            @PathVariable Long optionId
    ){
        return ResponseEntity.ok(optionService.deleteOption(optionId));
    }
}
