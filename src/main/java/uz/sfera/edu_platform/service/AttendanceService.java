package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.*;
import uz.sfera.edu_platform.entity.enums.WeekDay;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.AttendDto;
import uz.sfera.edu_platform.payload.AttendanceDto;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.ResAttend;
import uz.sfera.edu_platform.repository.AttendanceRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    public ApiResponse create(List<AttendanceDto> attendanceDtos, Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        for (AttendanceDto attendanceDto : attendanceDtos) {
            User student = userRepository.findById(attendanceDto.getStudentId())
                    .orElseThrow(() -> new NotFoundException("user not found"));

            if (attendanceRepository.findByStudentAndDate(student, attendanceDto.getDate()) == null) {
                Attendance attendance = Attendance.builder()
                        .student(student)
                        .date(attendanceDto.getDate())
                        .attendance(attendanceDto.isAttendance())
                        .group(group)
                        .build();
                attendanceRepository.save(attendance);
            }
        }
        return new ApiResponse("Attendance successfully saved");
    }


    @Transactional
    public ApiResponse getAttendanceByGroupId(Long groupId, int year, int month) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(
                        ResponseError.NOTFOUND("group not found"))));

        LocalDate startOfMonth = LocalDate.of(year, Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        LocalDate groupMonthYear = group.getStartDate().withDayOfMonth(1);

        if (startOfMonth.isBefore(groupMonthYear)) {
            return new ApiResponse(ResponseError.NOTFOUND("Attendance"));
        }

        List<LocalDate> groupDays = getGroupDays(groupId, year, month);

        List<ResAttend> resAttends = new ArrayList<>();

        List<User> users = userRepository.findAllByGroupId(group.getId());

        for (User user : users) {
            List<AttendDto> attendDtoList = new ArrayList<>();

            for (LocalDate groupDay : groupDays) {
                Attendance attendance = attendanceRepository.findByStudentAndDate(user, groupDay);

                AttendDto attendDto;
                if (attendance != null) {
                    attendDto = AttendDto.builder()
                            .id(attendance.getId())
                            .attendance(attendance.getAttendance())
                            .date(attendance.getDate())
                            .build();
                } else {
                    attendDto = AttendDto.builder()
                            .id(null)
                            .attendance(null)
                            .date(groupDay)
                            .build();
                }

                attendDtoList.add(attendDto);
            }

            ResAttend resAttend = ResAttend.builder()
                    .studentId(user.getId())
                    .fullName(user.getFullName())
                    .attendList(attendDtoList)
                    .build();

            resAttends.add(resAttend);
        }

        return new ApiResponse(resAttends);
    }

    @Transactional
    public ApiResponse getAttendanceByUser(User user, int year, int month) {
        Group group = groupRepository.findGroup(user.getId());
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Attendance not found"));
        }

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate groupMonthYear = group.getStartDate().withDayOfMonth(1);

        if (startOfMonth.isBefore(groupMonthYear)) {
            return new ApiResponse(ResponseError.NOTFOUND("Attendance"));
        }

        List<LocalDate> groupDays = getGroupDays(group.getId(), year, month);

        List<AttendDto> attendDtoList = groupDays.stream()
                .map(groupDay -> mapAttendance(user, groupDay))
                .collect(Collectors.toList());

        ResAttend resAttend = ResAttend.builder()
                .studentId(user.getId())
                .fullName(user.getFullName())
                .attendList(attendDtoList)
                .build();

        return new ApiResponse(Collections.singletonList(resAttend));
    }

    private AttendDto mapAttendance(User user, LocalDate date) {
        Attendance attendance = attendanceRepository.findByStudentAndDate(user, date);
        return AttendDto.builder()
                .id(attendance != null ? attendance.getId() : null)
                .attendance(attendance != null ? attendance.getAttendance() : null)
                .date(date)
                .build();
    }




//    public ApiResponse getAttendanceByStudent(User user, int month) {
//        LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(month), 1);
//        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
//        User user1 = userRepository.findById(user.getId())
//                .orElseThrow(() -> new NotFoundException("user not found"));
//        List<Attendance> attendance = attendanceRepository
//                .getAttendanceByStudentIdAndDateBetween(user1.getId(), startOfMonth, endOfMonth);
//        List<AttendanceDto> attendanceDtos = attendanceDtoList(attendance);
//        return new ApiResponse(attendanceDtos);
//    }


    public ApiResponse updateAttendance(AttendanceDto attendanceDto, Long attendanceId, User user) {
        Optional<Attendance> byId = attendanceRepository.findById(attendanceId);
        if (byId.isEmpty()) return new ApiResponse("attendance not foud");
        Attendance attendance = byId.get();
        attendance.setAttendance(attendanceDto.isAttendance());
        return new ApiResponse("Attendance updated");
    }


//    private List<AttendanceDto> attendanceDtoList(List<Attendance> attendances) {
//        return attendances.stream().map(attendance1 ->
//                AttendanceDto.builder()
//                        .id(attendance1.getId())
//                        .fullName(attendance1.getStudent().getFullName())
//                        .attendance(attendance1.getAttendance())
//                        .date(attendance1.getDate())
//                        .build()).toList();
//    }

//    public List<LocalDate> getGroupDays(Long groupId, int year, int month) {
//        Group group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new NotFoundException("group not found"));
//
//        GraphicDay days = group.getDays();
//        if (days == null || days.getWeekDay() == null || days.getWeekDay().isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        // Hafta kunlarini to‘plamga o‘tkazish
//        Set<DayOfWeek> weekDays = new HashSet<>(days.getWeekDay());
//
//        System.out.println(weekDays);
//
//        LocalDate startOfMonth = LocalDate.of(year, Month.of(month), 1);
//        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
//
//        List<LocalDate> classDates = new ArrayList<>();
//
//        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
//            for (DayOfWeek day : weekDays) {
//                if (day.getDayOfWeek().name().equals(date.getDayOfWeek().name())) {
//                    classDates.add(date);
//                }
//            }
//        }
//        return classDates;
//    }

    public List<LocalDate> getGroupDays(Long groupId, int year, int month) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        // WeekDay enum qiymatlarini olish
        Set<WeekDay> weekDays = new HashSet<>(Optional.ofNullable(group.getDays())
                .map(GraphicDay::getWeekDay)
                .orElse(Collections.emptyList()));

        if (weekDays.isEmpty()) return Collections.emptyList();

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return startOfMonth.datesUntil(endOfMonth.plusDays(1))
                .filter(date -> weekDays.contains(WeekDay.valueOf(date.getDayOfWeek().name()))) // Java DayOfWeek ni WeekDay ga moslashtiramiz
                .collect(Collectors.toList());
    }


}
