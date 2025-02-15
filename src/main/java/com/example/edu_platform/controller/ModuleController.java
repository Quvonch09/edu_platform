package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ModuleRequest;
import com.example.edu_platform.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/module")
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(
            @RequestBody ModuleRequest moduleRequest
    ){
        return ResponseEntity.ok(moduleService.createModule(moduleRequest));
    }

    @GetMapping("/get/{moduleId}")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(moduleService.getModule(moduleId));
    }

    @PutMapping("/update/{moduleId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long moduleId,
            @RequestBody ModuleRequest moduleRequest
    ){
        return ResponseEntity.ok(moduleService.update(moduleId, moduleRequest));
    }

    @DeleteMapping("/delete/{moduleId}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long moduleId
    ){
        return ResponseEntity.ok(moduleService.delete(moduleId));
    }
}
