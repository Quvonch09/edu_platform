package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.RoomDTO;
import com.example.edu_platform.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Room qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveRoom(@RequestBody RoomDTO roomDTO){
        ApiResponse apiResponse = roomService.saveRoom(roomDTO);
        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Room kurish")
    public ResponseEntity<ApiResponse> getRoomById(@PathVariable Long roomId){
        ApiResponse apiResponse = roomService.getRoomById(roomId);
        return ResponseEntity.ok(apiResponse);
    }
}
