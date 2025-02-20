package com.example.edu_platform.service;

import com.example.edu_platform.entity.Group;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.entity.enums.UserStatus;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.StudentDTO;
import com.example.edu_platform.payload.auth.ResponseLogin;
import com.example.edu_platform.payload.req.ReqStudent;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.payload.res.ResStudent;
import com.example.edu_platform.repository.FileRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.HomeworkRepository;
import com.example.edu_platform.repository.UserRepository;
import com.example.edu_platform.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
    public ApiResponse saveStudent(ReqStudent reqStudent){
        boolean b = userRepository.existsByPhoneNumberAndRoleAndEnabledTrue(
                reqStudent.getPhoneNumber(), Role.ROLE_STUDENT);

        if(b){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Student"));
        }

        Group group = groupRepository.findById(reqStudent.getGroupId()).orElse(null);
        if(group == null){
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        User student = User.builder()
                .fullName(reqStudent.getFullName())
                .phoneNumber(reqStudent.getPhoneNumber())
                .role(Role.ROLE_STUDENT)
                .age(reqStudent.getAge())
                .parentPhoneNumber(reqStudent.getParentPhoneNumber())
                .file(fileRepository.findById(reqStudent.getFileId()).orElse(null))
                .password(passwordEncoder.encode(reqStudent.getPassword()))
                .enabled(true)
                .userStatus(UserStatus.UQIYAPDI)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .build();

        group.getStudents().add(student);
        userRepository.save(student);
        groupRepository.save(group);
        return new ApiResponse("Successfully saved student");
    }



    public ApiResponse searchStudent(String fullName, String phoneNumber,
                                     UserStatus userStatus, String groupName,
                                     Long teacherId, Integer startAge, Integer endAge,
                                     int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ResStudent> users = userRepository.searchStudents(fullName, phoneNumber, userStatus.name(), groupName,
                teacherId, startAge, endAge, pageRequest);
        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .body(users.getContent())
                .build();
        return new ApiResponse(resPageable);

    }



    public ApiResponse getOneStudent(Long studentId){
        User user = userRepository.findById(studentId).orElse(null);
        if(user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        Group byStudentId = groupRepository.findByStudentId(user.getId()).orElse(null);


        StudentDTO studentDTO = StudentDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .parentPhoneNumber(user.getParentPhoneNumber())
                .age(user.getAge())
                .status(user.getUserStatus() != null ? user.getUserStatus().name() : null)
                .score(homeworkRepository.countByBall(user.getId()))
                .groupId(byStudentId != null ? byStudentId.getId() : null)
                .startStudyDate(user.getCreatedAt())
                .build();
        return new ApiResponse(studentDTO);
    }


    public ApiResponse updateStudent(Long studentId, ReqStudent reqStudent){
        boolean b = userRepository.existsByPhoneNumberAndRoleAndEnabledTrue(reqStudent.getPhoneNumber(), Role.ROLE_STUDENT);
        if(b){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Student"));
        }
        User user = userRepository.findById(studentId).orElse(null);
        if(user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        user.setFullName(reqStudent.getFullName());
        user.setPhoneNumber(reqStudent.getPhoneNumber());
        user.setParentPhoneNumber(reqStudent.getParentPhoneNumber());
        user.setAge(reqStudent.getAge());
        user.setFile(fileRepository.findById(reqStudent.getFileId()).orElse(null));
        user.setPassword(passwordEncoder.encode(reqStudent.getPassword()));
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getPhoneNumber());
        ResponseLogin responseLogin = new ResponseLogin(token,user.getRole().name(), user.getId());
        return new ApiResponse(responseLogin);
    }


    @Transactional
    public ApiResponse deleteStudent(Long studentId, LocalDate departureDate, String departureDescription){
        User user = userRepository.findById(studentId).orElse(null);
        if(user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        user.setUserStatus(UserStatus.CHIQIB_KETGAN);
        user.setEnabled(false);
        user.setDeparture_date(departureDate);
        user.setDeparture_description(departureDescription);
        return new ApiResponse("Successfully deleted student");
    }
}
