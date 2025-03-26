package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.TestGroup;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserStatus;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.StudentDTO;
import uz.sfera.edu_platform.payload.TestGroupDTO;
import uz.sfera.edu_platform.payload.req.ReqStudent;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.TestGroupRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestGroupService {
    private final TestGroupRepository testGroupRepository;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ApiResponse createGroup(String name){
        TestGroup testGroup = TestGroup.builder()
                .name(name)
                .active(true)
                .students(null)
                .build();

        testGroupRepository.save(testGroup);

        return new ApiResponse("Test guruh yaratildi");
    }

    public ApiResponse getGroups(String name,Boolean active,int page,int size){
        Page<TestGroup> testGroups = testGroupRepository.search(name,active, PageRequest.of(page,size));

        if (testGroups.getTotalElements() == 0){
            return new ApiResponse(ResponseError.NOTFOUND("Test guruhlar"));
        }

        List<TestGroupDTO> testGroupDTOList = testGroups.stream()
                .map(this::convertDTO)
                .toList();

        return new ApiResponse( ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(testGroups.getTotalPages())
                .totalElements(testGroups.getTotalElements())
                .body(testGroupDTOList)
                .build()
        );
    }

    public ApiResponse getList(){
        List<TestGroup> testGroups = testGroupRepository.findAll();

        if (testGroups.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("test guruhlar"));
        }

        List<TestGroupDTO> testGroupDTOList = testGroups.stream()
                .map(this::convertDTO)
                .toList();

        return new ApiResponse(testGroupDTOList);
    }

    public ApiResponse getStudentsByGroup(Long groupId){
        TestGroup testGroup = testGroupRepository.findById(groupId).orElse(null);
        if (testGroup == null){
            return new ApiResponse(ResponseError.NOTFOUND("TestGroup"));
        }

        List<User> students = userRepository.findByTestGroup(testGroup.getId());

        if (students.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Studentlar"));
        }

        List<StudentDTO> studentDTOList = students.stream()
                .map(this::convertStudentDTO)
                .toList();

        return new ApiResponse(studentDTOList);
    }

    public ApiResponse delete(Long groupId){
        TestGroup testGroup = testGroupRepository.findById(groupId).orElse(null);

        if (testGroup == null){
            return new ApiResponse(ResponseError.NOTFOUND("Test guruh"));
        }

        testGroup.setActive(false);
        testGroupRepository.save(testGroup);

        return new ApiResponse("Test guruh o'chirildi");
    }

    @Transactional
    public ApiResponse addStudent(ReqStudent reqStudent){
        TestGroup testGroup = testGroupRepository.findById(reqStudent.getGroupId()).orElse(null);

        if (testGroup == null || !testGroup.isActive()){
            return new ApiResponse(ResponseError.NOTFOUND("Test guruh"));
        }
        if (userRepository.existsByPhoneNumberAndEnabled(reqStudent.getPhoneNumber(), false)){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Student"));
        }

        User student = createStudent(reqStudent);

        testGroupRepository.addStudentToGroup(testGroup.getId(), student.getId());

        return new ApiResponse("Student ro'yxatga qo'shildi");
    }

    @Transactional
    public ApiResponse redirectStudent(Long studentId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        User student = userRepository.findById(studentId).orElse(null);
        TestGroup testGroup = testGroupRepository.findByStudentId(studentId);

        if (student == null || student.isDeleted() || !student.getRole().equals(Role.ROLE_STUDENT) || testGroup == null || !testGroup.isActive()) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }
        if (group == null || !group.isActive()) {
            return new ApiResponse(ResponseError.NOTFOUND("Guruh"));
        }

        student.setEnabled(true);
        student.setUserStatus(UserStatus.UQIYAPDI);
        userRepository.save(student);

        if (testGroupRepository.existsByStudentId(studentId)) {
            testGroupRepository.deleteByStudentId(studentId);
        }

        if (!groupRepository.existByStudentId(studentId)) {
            groupRepository.addStudentToGroup(groupId, studentId);
        } else {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Student"));
        }

        return new ApiResponse("Student ko'chirildi");
    }


    public ApiResponse deleteStudent(Long studentId){
        User student = userRepository.findById(studentId).orElse(null);
        TestGroup testGroup = testGroupRepository.findByStudentId(studentId);

        if (student == null || student.isDeleted() || !student.getRole().equals(Role.ROLE_STUDENT) || testGroup == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        testGroupRepository.deleteByStudentId(studentId);

        return new ApiResponse("Student o'chirildi");
    }

    private User createStudent(ReqStudent reqStudent){
        User student = User.builder()
                .fullName(reqStudent.getFullName())
                .role(Role.ROLE_STUDENT)
                .deleted(false)
                .enabled(false)
                .phoneNumber(reqStudent.getPhoneNumber())
                .parentPhoneNumber(reqStudent.getParentPhoneNumber())
                .password(encoder.encode(reqStudent.getPassword()))
                .age(reqStudent.getAge())
                .build();

        return userRepository.save(student);
    }

    private StudentDTO convertStudentDTO(User student){
        return StudentDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .age(student.getAge())
                .parentPhoneNumber(student.getParentPhoneNumber())
                .phoneNumber(student.getPhoneNumber())
                .build();
    }

    private TestGroupDTO convertDTO(TestGroup testGroup){
        return TestGroupDTO.builder()
                .id(testGroup.getId())
                .name(testGroup.getName())
                .active(testGroup.isActive())
                .studentCount(userRepository.findByTestGroup(testGroup.getId()).size())
                .createdAt(testGroup.getCreatedAt())
                .build();
    }
}
