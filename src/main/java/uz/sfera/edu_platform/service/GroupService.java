package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.*;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserStatus;
import uz.sfera.edu_platform.entity.enums.WeekDay;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.GroupDTO;
import uz.sfera.edu_platform.payload.GroupListDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqGroup;
import uz.sfera.edu_platform.payload.res.ResGroup;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final GraphicDayRepository graphicDayRepository;
    private final LessonRepository lessonRepository;
    private final ChatGroupService chatGroupService;

    @Transactional
    public ApiResponse saveGroup(ReqGroup reqGroup) {
        if (groupRepository.existsByName(reqGroup.getGroupName())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Group"));
        }

        Category category = findByIdOrThrow(categoryRepository, reqGroup.getCategoryId(), "Category");
        User teacher = findByIdOrThrow(userRepository, reqGroup.getTeacherId(), "Teacher");
        Room room = findByIdOrThrow(roomRepository, reqGroup.getRoomId(), "Room");

        if (isRoomOccupied(room.getId(), reqGroup.getStartTime(), reqGroup.getEndTime(),reqGroup.getDayIds(),null)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu vaqtda xona band"));
        }

        Group group = saveGroup(reqGroup, category, teacher, room);
        chatGroupService.createChatGroup(teacher, group);
        return new ApiResponse("Successfully saved group");
    }


    @Transactional
    public ApiResponse search(User user, String groupName, String teacherName, LocalDate startDate,
                              LocalDate endDate, Long categoryId, int page, int size) {
        boolean isTeacher = user.getRole().equals(Role.ROLE_TEACHER);
        Long teacherId = isTeacher ? user.getId() : null; // teacherId dinamik belgilanadi

        Page<Group> groups;
        if (startDate != null && endDate != null) {
            groups = groupRepository.searchGroupDate(groupName, teacherName, startDate, endDate, categoryId, teacherId,
                    PageRequest.of(page, size));
        } else if (startDate != null || endDate != null) {
            LocalDate date = startDate != null ? startDate : endDate;
            groups = groupRepository.searchGroupDate(groupName, teacherName, categoryId, teacherId, date, startDate != null,
                    PageRequest.of(page, size));
        } else {
            groups = groupRepository.searchGroup(groupName, teacherName, categoryId, teacherId,
                    PageRequest.of(page, size));
        }

        List<GroupDTO> list = groups.getContent().stream().map(group -> {
            GraphicDay graphicDay = group.getDays();

            List<String> days = Optional.ofNullable(group.getDays())
                    .map(gd -> gd.getWeekDay().stream().map(Enum::name).collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

            return convertGroupToGroupDTO(group, days, graphicDay);
        }).collect(Collectors.toList());

        return new ApiResponse(ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(groups.getTotalPages())
                .totalElements(groups.getTotalElements())
                .body(list)
                .build());
    }


    @Transactional
    public ApiResponse getGroupByTeacher(User teacher){
        List<Group> byTeacherId = groupRepository.findByTeacherId(teacher.getId());

        Map<Long, GraphicDay> graphicDays = graphicDayRepository.findAllByGroupIds(
                byTeacherId.stream().map(Group::getId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(GraphicDay::getId, Function.identity()));

        List<GroupDTO> list = byTeacherId.stream().map(group -> {
            GraphicDay graphicDay = graphicDays.get(group.getId()); // Xatolikni oldini olish

            List<String> days = Optional.ofNullable(group.getDays())
                    .map(gd -> gd.getWeekDay().stream().map(Enum::name).collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

            return convertGroupToGroupDTO(group, days, graphicDay);
        }).toList();

        return new ApiResponse(list);
    }


    public ApiResponse getOneGroup(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null){
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        Category category = group.getCategory();
        User teacher = group.getTeacher();

        List<String> days = Optional.ofNullable(group.getDays())
                .map(d -> d.getWeekDay().stream().map(Enum::name).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        List<User> list = groupRepository.findByGroup(groupId);

        ResGroup resGroup = ResGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .categoryName(getSafeValue(category, Category::getName))
                .teacherName(getSafeValue(teacher, User::getFullName))
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .active(group.isActive())
                .studentCount(list != null ? list.size() : 0)
                .countEndMonth((int) ChronoUnit.MONTHS.between(LocalDate.now(), group.getEndDate()))
                .countAllLessons(category != null ? lessonRepository.countLessonsByCategoryId(category.getId()) : 0)
                .countGroupLessons(groupRepository.countGroupLessons(group.getId()))
                .departureStudentCount(groupRepository.countGroup(group.getId()))
                .days(days)
                .build();

        return new ApiResponse(resGroup);
    }


    private <T, R> R getSafeValue(T obj, Function<T, R> mapper) {
        return Optional.ofNullable(obj).map(mapper).orElse(null);
    }


    public ApiResponse getGroupsList(User user) {
        List<Group> groups = user.getRole().equals(Role.ROLE_TEACHER)
                ? groupRepository.findByTeacherId(user.getId())
                : groupRepository.findAllByActiveTrue();

        List<GroupListDTO> groupDTOList = groups.stream()
                .map(group -> GroupListDTO.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .categoryName(group.getCategory() != null ? group.getCategory().getName() : null)
                        .teacherName(group.getTeacher() != null ? group.getTeacher().getFullName() : null)
                        .startDate(group.getStartDate())
                        .endDate(group.getEndDate())
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse(groupDTOList);
    }


    @Transactional
    public ApiResponse updateGroup(Long groupId, ReqGroup reqGroup) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        Room room = roomRepository.findById(reqGroup.getRoomId()).orElse(null);
        if (room == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Room"));
        }

        Category category = categoryRepository.findById(reqGroup.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        User teacher = userRepository.findById(reqGroup.getTeacherId()).orElse(null);
        if (teacher == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }
        GraphicDay oldGraphicDay = group.getDays();

        if (isRoomOccupied(room.getId(), reqGroup.getStartTime(), reqGroup.getEndTime(), reqGroup.getDayIds(), groupId)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu vaqtda xona band"));
        }
        if (oldGraphicDay != null) {
            graphicDayRepository.delete(oldGraphicDay);
            group.setDays(null);
        }

        GraphicDay graphicDay = new GraphicDay();
        graphicDay.setRoom(room);
        graphicDay.setWeekDay(weekDayList(reqGroup.getDayIds()));
        graphicDay.setStartTime(reqGroup.getStartTime());
        graphicDay.setEndTime(reqGroup.getEndTime());

        graphicDay = graphicDayRepository.save(graphicDay);

        group.setName(reqGroup.getGroupName());
        group.setCategory(category);
        group.setTeacher(teacher);
        group.setStartDate(reqGroup.getStartDate());
        group.setDays(graphicDay);

        groupRepository.save(group);

        return new ApiResponse("Successfully updated group");
    }

    public ApiResponse deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);

        if (group == null){
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        group.setActive(false);
        groupRepository.save(group);

        return new ApiResponse("Group o'chirildi");
    }


    @Scheduled(cron = "0 0 7 * * *")
     public void endGroup() {
        List<Group> groups = groupRepository.findByEndDate(LocalDate.now());

        for (Group group : groups) {
            group.setActive(false);
            for (User student : group.getStudents()) {
                student.setUserStatus(UserStatus.TUGATGAN);
                userRepository.save(student);
            }
            groupRepository.save(group);
        }

        log.info("Bugun {} ta guruh tugatdi !", groups.size());
    }



    private GroupDTO convertGroupToGroupDTO(Group group, List<String> weekDays, GraphicDay graphicDay){

        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .categoryName(group.getCategory() != null ? group.getCategory().getName() : null)
                .categoryId(group.getCategory() != null ? group.getCategory().getId() : null)
                .teacherName(group.getTeacher() != null ? group.getTeacher().getFullName() : null)
                .teacherId(group.getTeacher() != null ? group.getTeacher().getId() : null)
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .active(group.isActive())
                .studentCount(group.getStudents().size())
                .countEndMonth(calculateCountEndMonth(group.getEndDate()))
                .countAllLessons( group.getCategory() != null ?
                        lessonRepository.countLessonsByCategoryId(group.getCategory().getId()) : 0)
                .countGroupLessons(groupRepository.countGroupLessons(group.getId()))
                .departureStudentCount(groupRepository.countGroup(group.getId()))
                .weekDays(weekDays)
                .roomName(graphicDay != null ? graphicDay.getRoom().getName() : null)
                .roomId(graphicDay != null ? graphicDay.getRoom().getId() : null)
                .startTime(graphicDay != null ? graphicDay.getStartTime() : null)
                .endTime(graphicDay != null ? graphicDay.getEndTime() : null)
                .build();
    }



    private int calculateCountEndMonth(LocalDate endDate) {
        LocalDate now = LocalDate.now();
        long countDate = ChronoUnit.DAYS.between(now, endDate);

        if (countDate <= 32) {
            return 1;
        } else if (countDate <= 64) {
            return 2;
        } else {
            return 3;
        }
    }

    private List<WeekDay> weekDayList(List<Long> daysId) {
        return daysId.stream()
                .map(id -> switch (id.intValue()) {
                    case 1 -> WeekDay.MONDAY;
                    case 2 -> WeekDay.TUESDAY;
                    case 3 -> WeekDay.WEDNESDAY;
                    case 4 -> WeekDay.THURSDAY;
                    case 5 -> WeekDay.FRIDAY;
                    case 6 -> WeekDay.SATURDAY;
                    case 7 -> WeekDay.SUNDAY;
                    default -> null; // Noto'g'ri ID kiritilganda null bo'ladi
                })
                .filter(Objects::nonNull) // null qiymatlarni chiqarib tashlaydi
                .collect(Collectors.toList());
    }


    private <T> T findByIdOrThrow(JpaRepository<T, Long> repository, Long id, String entityName) {
        return repository.findById(id).orElse(null);
    }


    // Xona bandligini tekshirish uchun metod
    private boolean isRoomOccupied(Long roomId, LocalTime startTime, LocalTime endTime,List<Long> days,Long groupId) {
        return graphicDayRepository.existsOverlappingLesson(roomId, startTime, endTime,days,groupId);
    }

    public Group saveGroup(ReqGroup reqGroup, Category category, User teacher, Room room) {
        GraphicDay day = saveGraphicDay(reqGroup, room);
        Group build = Group.builder()
                .name(reqGroup.getGroupName())
                .category(category)
                .teacher(teacher)
                .days(day)
                .active(true)
                .startDate(reqGroup.getStartDate())
                .endDate(reqGroup.getStartDate().plusMonths(category.getDuration()))
                .build();
        return groupRepository.save(build);
    }

    public GraphicDay saveGraphicDay(ReqGroup reqGroup, Room room) {
        GraphicDay buildGraphic = GraphicDay.builder()
                .room(room)
                .weekDay(weekDayList(reqGroup.getDayIds()))
                .startTime(reqGroup.getStartTime())
                .endTime(reqGroup.getEndTime())
                .build();
        return graphicDayRepository.save(buildGraphic);
    }


}