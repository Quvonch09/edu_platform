package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.DayOfWeek;
import uz.sfera.edu_platform.entity.GraphicDay;
import uz.sfera.edu_platform.entity.Room;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.RoomDTO;
import uz.sfera.edu_platform.payload.req.ReqRoom;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResRoom;
import uz.sfera.edu_platform.repository.DayOfWeekRepository;
import uz.sfera.edu_platform.repository.GraphicDayRepository;
import uz.sfera.edu_platform.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final GraphicDayRepository graphicDayRepository;
    private final DayOfWeekRepository dayOfWeekRepository;


    public ApiResponse saveRoom(ReqRoom reqRoom) {
        if (roomRepository.existsByName(reqRoom.getName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu nomli xona"));
        }

        LocalTime startTime = reqRoom.getStartTime();
        LocalTime endTime = reqRoom.getEndTime();

        if (graphicDayRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(reqRoom.getId(), startTime, endTime)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu vaqtda xona band"));
        }

        Room room = roomRepository.save(Room.builder()
                .name(reqRoom.getName())
                .color(reqRoom.getColor())
                .build());

        List<DayOfWeek> dayOfWeeks = dayOfWeekRepository.findAllById(reqRoom.getWeekDays());
        if (dayOfWeeks.size() != reqRoom.getWeekDays().size()) {
            return new ApiResponse(ResponseError.NOTFOUND("WeekDays"));
        }

        GraphicDay graphicDay = graphicDayRepository.save(GraphicDay.builder()
                .startTime(startTime)
                .endTime(endTime)
                .weekDay(dayOfWeeks)
                .room(room)
                .build());

        room.setGraphicDayId(graphicDay.getId());
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


        GraphicDay graphicDay = graphicDayRepository.findByRoomId(room.getId()).orElse(null);

        List<DayOfWeek> dayOfWeeks = reqRoom.getWeekDays().stream()
                .map(weekDay -> dayOfWeekRepository.findById(weekDay).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (dayOfWeeks.size() != reqRoom.getWeekDays().size()) {
            return new ApiResponse(ResponseError.NOTFOUND("WeekDays"));
        }


        graphicDay.setStartTime(reqRoom.getStartTime());
        graphicDay.setEndTime(reqRoom.getEndTime());
        graphicDay.setWeekDay(dayOfWeeks);
        graphicDay.setRoom(room);

        roomRepository.save(room);
        graphicDayRepository.save(graphicDay);

        return new ApiResponse("Successfully updated");
    }


    public ApiResponse deleteRoom(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        graphicDayRepository.findByRoomId(room.getId())
                .ifPresent(graphicDayRepository::delete);

        roomRepository.delete(room);
        return new ApiResponse("Successfully deleted");
    }

}
