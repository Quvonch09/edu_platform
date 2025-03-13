package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserStatus;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.StudentDTO;
import uz.sfera.edu_platform.payload.UserDTO;
import uz.sfera.edu_platform.payload.auth.ResponseLogin;
import uz.sfera.edu_platform.payload.req.ReqStudent;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResStudent;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.HomeworkRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import uz.sfera.edu_platform.security.JwtProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final HomeworkRepository homeworkRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public ApiResponse saveStudent(ReqStudent reqStudent) {
        if (userRepository.existsByPhoneNumberAndEnabledIsTrue(reqStudent.getPhoneNumber())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Student"));
        }

        Group group = groupRepository.findById(reqStudent.getGroupId()).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        File file = (reqStudent.getFileId() != null)
                ? fileRepository.findById(reqStudent.getFileId()).orElse(null)
                : null;

        User student = User.builder()
                .fullName(reqStudent.getFullName())
                .phoneNumber(reqStudent.getPhoneNumber())
                .role(Role.ROLE_STUDENT)
                .age(reqStudent.getAge())
                .parentPhoneNumber(reqStudent.getParentPhoneNumber())
                .file(file)
                .password(passwordEncoder.encode(reqStudent.getPassword()))
                .enabled(true)
                .deleted(false)
                .userStatus(UserStatus.UQIYAPDI)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .build();

        userRepository.save(student);
        group.getStudents().add(student);
        groupRepository.save(group);

        return new ApiResponse("Successfully saved student");
    }


    public ApiResponse searchStudent(User user, String fullName, String phoneNumber,
                                     UserStatus userStatus, String groupName,
                                     Long teacherId, Integer startAge, Integer endAge, Boolean hasPaid,
                                     int page, int size) {

        if (user.getRole().equals(Role.ROLE_TEACHER)) {
            teacherId = user.getId();
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        String userStatusStr = (userStatus != null) ? userStatus.name() : null;

        Page<ResStudent> users = userRepository.searchStudents(
                fullName, phoneNumber, userStatusStr, groupName,
                teacherId, startAge, endAge, hasPaid, pageRequest
        );

        if (users.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Hech qanday student"));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .body(users.getContent())
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getOneStudent(Long studentId) {
        User user = userRepository.findById(studentId).orElse(null);
        if (user == null || user.isDeleted()) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }
        Group group = groupRepository.findByStudentId(user.getId()).orElse(null);

        StudentDTO studentDTO = StudentDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .parentPhoneNumber(user.getParentPhoneNumber())
                .age(user.getAge())
                .status(user.getUserStatus() != null ? user.getUserStatus().name() : null)
                .score(homeworkRepository.countByBall(user.getId())) // Avoid null score
                .groupId(group != null ? group.getId() : null)
                .startStudyDate(user.getCreatedAt())
                .departureDescription(user.getDeparture_description())
                .build();

        return new ApiResponse(studentDTO);
    }


    @Transactional
    public ApiResponse updateStudent(Long studentId, ReqStudent reqStudent) {
        User user = userRepository.findById(studentId).orElse(null);
        if (user == null || user.isEnabled()) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }
        Group newGroup = groupRepository.findById(reqStudent.getGroupId()).orElse(null);
        Group oldGroup = groupRepository.findByStudentId(user.getId()).orElse(null);
        if (oldGroup != null) {
            oldGroup.getStudents().remove(user);
            groupRepository.save(oldGroup);
        }

        user.setFullName(reqStudent.getFullName());
        user.setPhoneNumber(reqStudent.getPhoneNumber());
        user.setParentPhoneNumber(reqStudent.getParentPhoneNumber());
        user.setAge(reqStudent.getAge());
        user.setFile(fileRepository.findById(reqStudent.getFileId()).orElse(null));
        //todo password update qilish alohida api bo'lishi kerak
        if (reqStudent.getPassword() != null && !reqStudent.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(reqStudent.getPassword()));
        }

        //todo user gruppaga bunday qoshilmaydi
        newGroup.getStudents().add(user);

        // 🔴 Missing: Studentni yangi grga qushildi lekin grni saqlanmagan
        groupRepository.save(newGroup);

        userRepository.save(user);

        String token = jwtProvider.generateToken(user.getPhoneNumber());
        ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());

        return new ApiResponse(responseLogin);
    }



    public ApiResponse deleteStudent(Long studentId, LocalDate departureDate, String departureDescription) {
        User user = userRepository.findById(studentId).orElse(null);
        if (user == null || !user.isEnabled()) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }
         // Updating user fields
        user.setUserStatus(UserStatus.CHIQIB_KETGAN);
        user.setDeleted(true);
        user.setEnabled(false);
        user.setPhoneNumber(user.getPhoneNumber() + LocalDateTime.now() + "_deleted");
        user.setDeparture_date(departureDate);
        user.setDeparture_description(departureDescription);

        // 🔴 Missing: Malumot update qilingan lekin saqlanmagan xozir qushib quydim
        userRepository.save(user);

        return new ApiResponse("Successfully deleted student");
    }


    public ApiResponse getStudentGroupBy(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null || !group.isActive()) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        List<User> students = groupRepository.findByGroup(groupId);
        List<StudentDTO> list = students.stream().map(this::getDto).toList();

        return new ApiResponse(list);
    }


    public StudentDTO getDto(User user){
        return StudentDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .parentPhoneNumber(user.getParentPhoneNumber())
                .age(user.getAge())
                .status(user.getUserStatus() != null ? user.getUserStatus().name() : null)
                .score(homeworkRepository.countByBall(user.getId()))
                .startStudyDate(user.getCreatedAt())
                .departureDescription(user.getDeparture_description())
                .build();
    }


    public ApiResponse getTeacherByStudent(User user){
        List<User> users = Optional.ofNullable(userRepository.searchForUsers(user.getId()))
                .orElse(Collections.emptyList());  // ✅ NullPointerException oldini oladi

        List<UserDTO> list = users.stream()
                .map(this::getTeacherDTO)  // ✅ To‘g‘ri metod nomi
                .toList();

        if (list.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Userlar"));
        }

        return new ApiResponse(list);
    }


    public UserDTO getTeacherDTO(User user) {  // ✅ Metod nomi to‘g‘ri bo‘ldi
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
    }


}
