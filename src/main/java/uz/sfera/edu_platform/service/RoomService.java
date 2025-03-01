package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Room;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.RoomDTO;
import uz.sfera.edu_platform.payload.req.ReqRoom;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResRoom;
import uz.sfera.edu_platform.repository.GraphicDayRepository;
import uz.sfera.edu_platform.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;


    public ApiResponse saveRoom(ReqRoom reqRoom) {
        if (roomRepository.existsByName(reqRoom.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu nomli xona"));
        }


        Room room = roomRepository.save(Room.builder()
                .name(reqRoom.getName())
                .color(reqRoom.getColor())
                .startTime(reqRoom.getStartTime())
                .endTime(reqRoom.getEndTime())
                .build());
        roomRepository.save(room);


        return new ApiResponse("Successfully saved");
    }



    public ApiResponse getRooms(String name, String color, int page, int size){
        Page<ResRoom> allRooms = roomRepository.getAllRooms(name, color, PageRequest.of(page, size));

        return new ApiResponse(ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(allRooms.getTotalPages())
                .totalElements(allRooms.getTotalElements())
                .body(allRooms.getContent())
                .build());
    }


    public ApiResponse getRoomsList() {
        List<RoomDTO> resRooms = roomRepository.findAll().stream()
                .map(room -> RoomDTO.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .color(room.getColor())
                        .startTime(room.getStartTime())
                        .endTime(room.getEndTime())
                        .build())
                .toList();


        return new ApiResponse(resRooms);
    }


    public ApiResponse getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }


        RoomDTO roomDTO = RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .color(room.getColor())
                .startTime(room.getStartTime())
                .endTime(room.getEndTime())
                .build();

        return new ApiResponse(roomDTO);
    }




    public ApiResponse updateRoom(Long roomId,ReqRoom reqRoom){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }
        room.setName(reqRoom.getName());
        room.setColor(reqRoom.getColor());
        room.setStartTime(reqRoom.getStartTime());
        room.setEndTime(reqRoom.getEndTime());

        roomRepository.save(room);

        return new ApiResponse("Successfully updated");
    }


    public ApiResponse deleteRoom(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        roomRepository.delete(room);
        return new ApiResponse("Successfully deleted");
    }

}
