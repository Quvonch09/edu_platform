package com.example.edu_platform.service;

import com.example.edu_platform.entity.Room;
import com.example.edu_platform.mapper.RoomMapper;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.RoomDTO;
import com.example.edu_platform.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public ApiResponse saveRoom(RoomDTO roomDTO){
        roomRepository.save(roomMapper.toRoom(roomDTO));
        return new ApiResponse("Success");
    }

    public ApiResponse getRoomById(Long id){
        Room room = roomRepository.findById(id).orElse(null);
        if (room == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        return new ApiResponse(roomMapper.toDTO(room));
    }
}
