package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.GraphicDay;
import uz.sfera.edu_platform.entity.Room;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.RoomDTO;
import uz.sfera.edu_platform.payload.req.ReqRoom;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResRoom;
import uz.sfera.edu_platform.repository.GraphicDayRepository;
import uz.sfera.edu_platform.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final GraphicDayRepository graphicDayRepository;


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
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ResRoom> allRooms = roomRepository.getAllRooms(name, color, pageRequest);
        return new ApiResponse(ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(allRooms.getTotalPages())
                .totalElements(allRooms.getTotalElements())
                .body(allRooms.getContent())
                .build());
    }


    public ApiResponse getRoomsList() {
        List<Room> allRooms = roomRepository.findAll();
        Map<Long, GraphicDay> graphicDayMap = graphicDayRepository.findAll()
                .stream()
                .collect(Collectors.toMap(gd -> gd.getRoom().getId(), gd -> gd));

        List<RoomDTO> resRooms = allRooms.stream().map(room -> {
            GraphicDay graphicDay = graphicDayMap.get(room.getId());
            return RoomDTO.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .color(room.getColor())
                    .startTime(graphicDay != null ? graphicDay.getStartTime() : null)
                    .endTime(graphicDay != null ? graphicDay.getEndTime() : null)
                    .build();
        }).collect(Collectors.toList());

        return new ApiResponse(resRooms);
    }


    public ApiResponse getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        GraphicDay graphicDay = graphicDayRepository.findByRoomId(roomId).orElse(null);

        RoomDTO roomDTO = RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .color(room.getColor())
                .startTime(graphicDay != null ? graphicDay.getStartTime() : null)
                .endTime(graphicDay != null ? graphicDay.getEndTime() : null)
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
