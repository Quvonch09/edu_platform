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

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final GraphicDayRepository graphicDayRepository;
    private final DayOfWeekRepository dayOfWeekRepository;


    public ApiResponse saveRoom(ReqRoom reqRoom){
        boolean b = roomRepository.existsByName(reqRoom.getName());
        if (b){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu nomli xona"));
        }

        //Parse qilishda xatolik
        LocalTime startTime = LocalTime.parse(reqRoom.getStartTime());
        LocalTime endTime = LocalTime.parse(reqRoom.getEndTime());

        if (graphicDayRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(reqRoom.getId(),startTime,endTime)){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu vaqtda xona "));
        }

        Room room = Room.builder()
                .name(reqRoom.getName())
                .color(reqRoom.getColor())
                .build();

        Room save = roomRepository.save(room);

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        for (Integer weekDay : reqRoom.getWeekDays()) {
            DayOfWeek dayOfWeek = dayOfWeekRepository.findById(weekDay).orElse(null);
            if (dayOfWeek == null){
                return new ApiResponse(ResponseError.NOTFOUND("WeekDays"));
            }
            dayOfWeeks.add(dayOfWeek);
        }

        GraphicDay graphicDay = GraphicDay.builder()
                .startTime(startTime)
                .endTime(endTime)
                .weekDay(dayOfWeeks)
                .room(save)
                .build();

        GraphicDay day = graphicDayRepository.save(graphicDay);
        save.setGraphicDayId(day.getId());
        roomRepository.save(save);

        return new ApiResponse("Successfully saved");
    }


    public ApiResponse getRooms(String name, String color, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ResRoom> allRooms = roomRepository.getAllRooms(name, color, pageRequest);
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(allRooms.getTotalPages())
                .totalElements(allRooms.getTotalElements())
                .body(allRooms.getContent())
                .build();
        return new ApiResponse(resPageable);
    }


    public ApiResponse getRoomsList(){
        List<Room> all = roomRepository.findAll();
        List<RoomDTO> resRooms = new ArrayList<>();
        for (Room room : all) {
            GraphicDay graphicDay = graphicDayRepository.findByRoomId(room.getId()).orElse(null);
            RoomDTO roomDTO = RoomDTO.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .color(room.getColor())
                    .startTime(graphicDay != null ? graphicDay.getStartTime() : null)
                    .endTime(graphicDay != null ? graphicDay.getEndTime() : null)
                    .build();
            resRooms.add(roomDTO);
        }
        return new ApiResponse(resRooms);
    }


    public ApiResponse getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        GraphicDay graphicDay = graphicDayRepository.findByRoomId(room.getId()).orElse(null);

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

        LocalTime startTime = LocalTime.parse(reqRoom.getStartTime());
        LocalTime endTime = LocalTime.parse(reqRoom.getEndTime());

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        for (Integer weekDay : reqRoom.getWeekDays()) {
            DayOfWeek dayOfWeek = dayOfWeekRepository.findById(weekDay).orElse(null);
            if (dayOfWeek == null){
                return new ApiResponse(ResponseError.NOTFOUND("WeekDays"));
            }
            dayOfWeeks.add(dayOfWeek);
        }

        graphicDay.setStartTime(startTime);
        graphicDay.setEndTime(endTime);
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

        GraphicDay graphicDay = graphicDayRepository.findByRoomId(room.getId()).orElse(null);
        if (graphicDay == null){
            return new ApiResponse(ResponseError.NOTFOUND("Roomning grafigi"));
        }

        graphicDayRepository.delete(graphicDay);
        roomRepository.delete(room);
        return new ApiResponse("Successfully deleted");
    }
}
