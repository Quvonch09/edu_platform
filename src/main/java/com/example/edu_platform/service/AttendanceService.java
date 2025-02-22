package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.exception.NotFoundException;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.AttendDto;
import com.example.edu_platform.payload.AttendanceDto;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.res.ResAttend;
import com.example.edu_platform.repository.AttendanceRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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



    public ApiResponse getAttendanceByStudent(User user, int month) {
        LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        User user1 = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<Attendance> attendance = attendanceRepository
                .getAttendanceByStudentIdAndDateBetween(user1.getId(), startOfMonth, endOfMonth);
        List<AttendanceDto> attendanceDtos = attendanceDtoList(attendance);
        return new ApiResponse(attendanceDtos);
    }


    public ApiResponse updateAttendance(AttendanceDto attendanceDto, Long attendanceId, User user) {
        Optional<Attendance> byId = attendanceRepository.findById(attendanceId);
        if (byId.isEmpty()) return new ApiResponse("attendance not foud");
        Attendance attendance = byId.get();
        attendance.setAttendance(attendanceDto.isAttendance());
        return new ApiResponse("Attendance updated");
    }


    private List<AttendanceDto> attendanceDtoList(List<Attendance> attendances) {
        return attendances.stream().map(attendance1 ->
                AttendanceDto.builder()
                        .id(attendance1.getId())
                        .fullName(attendance1.getStudent().getFullName())
                        .attendance(attendance1.getAttendance())
                        .date(attendance1.getDate())
                        .build()).toList();
    }

    public List<LocalDate> getGroupDays(Long groupId, int year, int month) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("group not found"));

        GraphicDay days = group.getDays();

        LocalDate startOfMonth = LocalDate.of(year, Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<LocalDate> classDates = new ArrayList<>();

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            for (DayOfWeek day : days.getWeekDay()) {
                if (date.getDayOfWeek().name().equalsIgnoreCase(day.toString())) {
                    classDates.add(date);
                    break;
                }
            }
        }

        return classDates;
    }
}
