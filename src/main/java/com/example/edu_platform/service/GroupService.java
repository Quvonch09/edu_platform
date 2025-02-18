package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.GroupDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqGroup;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DayOfWeekRepository dayOfWeekRepository;
    private final GraphicDayRepository graphicDayRepository;

    public ApiResponse saveGroup(ReqGroup reqGroup){
        boolean b = groupRepository.existsByName(reqGroup.getGroupName());
        if(b){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Group"));
        }

        Category category = categoryRepository.findById(reqGroup.getCategoryId()).orElse(null);
        if(category == null){
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        User teacher = userRepository.findById(reqGroup.getTeacherId()).orElse(null);
        if(teacher == null){
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        Room room = roomRepository.findById(reqGroup.getRoomId()).orElse(null);
        if(room == null){
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        List<DayOfWeek> dayOfWeekList = null;

        if (reqGroup.getDayIds() != null) {
            dayOfWeekList = new ArrayList<>(dayOfWeekRepository.findAllById(reqGroup.getDayIds()));
        }

        LocalTime startDate = LocalTime.parse(reqGroup.getStartTime());
        LocalTime endDate = LocalTime.parse(reqGroup.getEndTime());

        boolean b1 = graphicDayRepository.existsByGraphicDayInGroup(room.getId(), startDate, endDate);
        if (b1){
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu vaqtda xona band"));
        }


        GraphicDay graphicDay = GraphicDay.builder()
                .room(room)
                .weekDay(dayOfWeekList)
                .startTime(startDate)
                .endTime(endDate)
                .build();
        graphicDayRepository.save(graphicDay);

        Group group = Group.builder()
                .name(reqGroup.getGroupName())
                .category(category)
                .teacher(teacher)
                .days(graphicDay)
                .active(true)
                .endDate(reqGroup.getStartDate().plusDays(category.getDuration()))
                .startDate(reqGroup.getStartDate())
                .build();
        groupRepository.save(group);
        return new ApiResponse("Successfully saved group");
    }


    @Transactional
    public ApiResponse search(String groupName,String teacherName, LocalDate startDate,
                              LocalDate endDate,Long categoryId, int page, int size){
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Group> groups = groupRepository.searchGroup(groupName, teacherName, startDate, endDate, pageRequest);
        List<GroupDTO> groupDTOList = new ArrayList<>();
        for (Group group : groups) {
            GroupDTO groupDTO = GroupDTO.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .categoryId(group.getCategory().getId())
                    .teacherName(group.getTeacher().getFullName())
                    .startDate(group.getStartDate())
                    .endDate(group.getEndDate())
                    .active(group.getActive())
                    .studentCount(group.getStudents().size())
                    .departureStudentCount(groupRepository.countGroup(group.getId()))
                    .build();
            groupDTOList.add(groupDTO);
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(groups.getTotalPages())
                .totalElements(groups.getTotalElements())
                .body(groupDTOList)
                .build();
        return new ApiResponse(resPageable);
    }


    @Transactional
    public ApiResponse getOneGroup(Long groupId){
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        GroupDTO groupDTO = GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .categoryId(group.getCategory().getId())
                .teacherName(group.getTeacher().getFullName())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .active(group.getActive())
                .studentCount(group.getStudents().size())
                .departureStudentCount(groupRepository.countGroup(group.getId()))
                .build();
        return new ApiResponse(groupDTO);
    }


    public ApiResponse updateGroup(Long groupId, ReqGroup reqGroup){
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        List<DayOfWeek> dayOfWeekList = null;

        if (reqGroup.getDayIds() != null) {
            dayOfWeekList = new ArrayList<>(dayOfWeekRepository.findAllById(reqGroup.getDayIds()));
        }

        GraphicDay graphicDay = graphicDayRepository.findGraphicDay(reqGroup.getRoomId()).orElse(null);
        LocalTime startDate = LocalTime.parse(reqGroup.getStartTime());
        LocalTime endDate = LocalTime.parse(reqGroup.getEndTime());

        boolean b1 = graphicDayRepository.existsByGraphicDayInGroup(reqGroup.getRoomId(), startDate, endDate);
        if (b1){
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu vaqtda xona band"));
        }
        if (graphicDay != null) {
            graphicDay.setWeekDay(dayOfWeekList);
            graphicDay.setStartTime(startDate);
            graphicDay.setEndTime(endDate);
            graphicDayRepository.save(graphicDay);
        }

        group.setName(reqGroup.getGroupName());
        group.setCategory(categoryRepository.findById(reqGroup.getCategoryId()).orElse(null));
        group.setTeacher(userRepository.findById(reqGroup.getTeacherId()).orElse(null));
        group.setStartDate(reqGroup.getStartDate());
        group.setDays(graphicDay);
        groupRepository.save(group);
        return new ApiResponse("Successfully updated group");
    }



    public ApiResponse deleteGroup(Long groupId){
        Group group = groupRepository.findById(groupId).orElse(null);
        if(group == null){
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        group.setActive(false);
        groupRepository.save(group);
        return new ApiResponse("Successfully deleted group");
    }
}
