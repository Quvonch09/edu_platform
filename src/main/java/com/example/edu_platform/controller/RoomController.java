package com.example.edu_platform.controller;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.req.ReqRoom;
import com.example.edu_platform.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Admin room qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveRoom(@RequestBody ReqRoom reqRoom){
        ApiResponse apiResponse = roomService.saveRoom(reqRoom);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN')")
    @Operation(summary = "Admin roomni search qilish")
    @GetMapping
    public ResponseEntity<ApiResponse> searchRoom(@RequestParam(required = false, value = "name") String name,
                                                  @RequestParam(required = false, value = "color") String color,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size){
        ApiResponse rooms = roomService.getRooms(name, color, page, size);
        return ResponseEntity.ok(rooms);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN')")
    @Operation(summary = "Admin roomni bittasini kurish")
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse> getRoom(@PathVariable Long roomId){
        ApiResponse roomById = roomService.getRoomById(roomId);
        return ResponseEntity.ok(roomById);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN')")
    @Operation(summary = " Admin roomni update qilish")
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse> updateRoom(@PathVariable Long roomId, @RequestBody ReqRoom reqRoom){
        ApiResponse roomById = roomService.updateRoom(roomId, reqRoom);
        return ResponseEntity.ok(roomById);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN')")
    @Operation(summary = "Admin roomni uchirish")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable Long roomId){
        ApiResponse roomById = roomService.deleteRoom(roomId);
        return ResponseEntity.ok(roomById);
    }
}
