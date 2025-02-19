package com.example.edu_platform.controller;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.security.CurrentUser;
import com.example.edu_platform.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Userga tegishli resultlar")
    public ResponseEntity<ApiResponse> getUserResults(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getUserResults(userId));
    }

    @GetMapping("/my-results")
    @Operation(summary = "User o'zining natijalar tarixi")
    public ResponseEntity<ApiResponse> getUserResultHistory(@CurrentUser User user) {
        return ResponseEntity.ok(resultService.getUserResultHistory(user));
    }

    @GetMapping("/{resultId}")
    @Operation(summary = "Id bo'yicha resultni ko'rish")
    public ResponseEntity<ApiResponse> getResultById(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.getResultById(resultId));
    }

    @DeleteMapping("/{resultId}")
    @Operation(summary = "resultni o'chirish")
    public ResponseEntity<ApiResponse> deleteResult(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.deleteResult(resultId));
    }
}
