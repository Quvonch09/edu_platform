package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.ChatStatus;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.*;
import uz.sfera.edu_platform.payload.auth.ResponseLogin;
import uz.sfera.edu_platform.payload.req.ReqAdmin;
import uz.sfera.edu_platform.payload.req.ReqTeacher;
import uz.sfera.edu_platform.payload.res.ResCategory;
import uz.sfera.edu_platform.payload.res.ResGroupDto;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResStudentCount;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import uz.sfera.edu_platform.security.JwtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final FileRepository fileRepository;
    private final JwtProvider jwtProvider;


    public ApiResponse saveTeacher(ReqTeacher reqTeacher) {
        if (reqTeacher == null || !StringUtils.hasText(reqTeacher.getFullName())) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Iltimos, to‘liq ma'lumot kiriting"));
        }

        if (userRepository.existsByPhoneNumberAndEnabledIsTrue(reqTeacher.getPhoneNumber())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu telefon raqam allaqachon mavjud"));
        }

        Category category = categoryRepository.findById(reqTeacher.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategoriya topilmadi"));

        File file = fileRepository.findById(reqTeacher.getFileId()).orElse(null);

        User teacher = User.builder()
                .fullName(reqTeacher.getFullName())
                .phoneNumber(reqTeacher.getPhoneNumber())
                .categories(List.of(category))  // Ortacha ro‘yxat yaratamiz
                .password(passwordEncoder.encode(reqTeacher.getPassword()))
                .enabled(true)
                .role(Role.ROLE_TEACHER)
                .file(file)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(teacher);

        return new ApiResponse("O‘qituvchi muvaffaqiyatli saqlandi");
    }


    public ApiResponse searchUsers(String fullName, String phoneNumber, Long groupId, Role role, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<User> allTeachers = userRepository.searchUsers(fullName, phoneNumber, groupId,
                role != null ? role.name() : null, pageRequest);

        List<TeacherDTO> teacherList = allTeachers.stream()
                .map(user -> convertUserToTeacherDTO(user,
                        user.getCategories().stream()
                                .map(category -> new ResCategory(category.getId(), category.getName()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(allTeachers.getTotalElements())
                .body(teacherList)
                .build();

        return new ApiResponse(resPageable);
    }


    private TeacherDTO convertUserToTeacherDTO(User user, List<ResCategory> categoryIds) {
        List<Group> groups = groupRepository.findGroup(user.getId());
        List<ResGroupDto> list = groups.isEmpty() ? new ArrayList<>() : groups.stream().map(this::getDto).toList();

        return TeacherDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .categories(categoryIds)
                .active(user.isEnabled())
                .groupCount(groups.size())
                .groupList(list)
                .fileId(Optional.ofNullable(user.getFile()).map(File::getId).orElse(null)) // Optimallashtirilgan
                .build();
    }
//    private TeacherDTO convertUserToTeacherDTO(User user) {
//        List<ResCategory> categories = user.getCategories().stream()
//                .map(category -> new ResCategory(category.getId(), category.getName()))
//                .collect(Collectors.toList());
//
//        return new TeacherDTO(user.getId(), user.getFullName(), user.getPhoneNumber(), categories,
//                null, null, null, null, null);
//    }


    public ApiResponse getUsersList(Role role) {
        List<User> users = (role != null) ? userRepository.findAllByRole(role) : userRepository.findAll();

        List<UserDTO> userDTOList = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return new ApiResponse(userDTOList);
    }

    public ApiResponse getOneTeacher(Long teacherId) {
        userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Teacher"))));

        List<ResStudentCount> res = groupRepository.findAllStudentsByTeacherId(teacherId);
        return new ApiResponse(res);
    }


    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
    }


    public ApiResponse updateTeacher(Long teacherId, ReqTeacher reqTeacher) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Teacher"))));

        user.setFullName(reqTeacher.getFullName());
        user.setPhoneNumber(reqTeacher.getPhoneNumber());

        if (reqTeacher.getPassword() != null && !reqTeacher.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(reqTeacher.getPassword()));
        }

        user.setFile(fileRepository.findById(reqTeacher.getFileId()).orElse(null));
        userRepository.save(user);

        return new ApiResponse("Teacher successfully updated");
    }


    public ApiResponse updateActiveTeacher(Long teacherId, Boolean active) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Teacher"))));

        user.setEnabled(active);
        userRepository.save(user);

        return new ApiResponse("Teacher successfully updated");
    }


    public ApiResponse deleteTeacher(Long userId, User currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Teacher"))));

        if (user.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getRole().equals(Role.ROLE_CEO)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Sizda adminni o‘chirishga huquq yo‘q"));
        }

        user.setEnabled(false);
        user.setPhoneNumber(user.getPhoneNumber() + "_" + UUID.randomUUID().toString().substring(0, 8) + "_deleted");

        userRepository.save(user);
        return new ApiResponse("User successfully deleted");
    }


    public ApiResponse saveAdmin(ReqAdmin reqAdmin){
        if (userRepository.existsByPhoneNumberAndEnabledIsTrue(reqAdmin.getPhoneNumber())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu Admin"));
        }

        User admin = User.builder()
                .fullName(reqAdmin.getFullName())
                .phoneNumber(reqAdmin.getPhoneNumber())
                .role(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode(reqAdmin.getPassword()))
                .file(reqAdmin.getFileId() != null ? fileRepository.findById(reqAdmin.getFileId()).orElse(null) : null)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(admin);
        return new ApiResponse("Admin successfully saved");
    }

    public ApiResponse getOneAdmin(Long adminId){
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Admin"))));

        if (!user.getRole().equals(Role.ROLE_ADMIN)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Admin topilmadi"));
        }

        AdminDTO adminDTO = AdminDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
        return new ApiResponse(adminDTO);
    }


    public ApiResponse updateAdmin(Long adminId, ReqAdmin reqAdmin){
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Admin"))));

        user.setFullName(reqAdmin.getFullName());
        user.setPhoneNumber(reqAdmin.getPhoneNumber());

        if (reqAdmin.getPassword() != null && !reqAdmin.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(reqAdmin.getPassword()));
        }

        userRepository.save(user);
        return new ApiResponse("Admin successfully updated");
    }


    public ApiResponse getMe(User user){
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .fileId(Optional.ofNullable(user.getFile()).map(File::getId).orElse(null)) // Optimallashtirilgan
                .build();
        return new ApiResponse(userDTO);
    }


    public ApiResponse updateUser(User user, UserDTO userDTO) {
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getFileId() == null) {
            user.setFile(null);
        } else {
            user.setFile(fileRepository.findById(userDTO.getFileId()).orElse(null));
        }

        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getPhoneNumber());
        ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
        return new ApiResponse(responseLogin);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("User"))));
    }

    public void onlineOffline(User user, boolean isActive) {
        ChatStatus newStatus = isActive ? ChatStatus.ONLINE : ChatStatus.OFFLINE;
        if (user.getChatStatus() != newStatus) {
            user.setChatStatus(newStatus);
            userRepository.save(user);
        }
    }

    public List<User> searchForChat(String fullName, String phone, String roleName) {
        List<User> users = userRepository.searchForChat(fullName, phone, roleName);
        return users != null ? users : new ArrayList<>();  // Agar null bo‘lsa, bo‘sh list qaytarish
    }


    public ApiResponse getTeacher(User user) {
        List<User> users = userRepository.searchForTeacher(user.getId());
        List<UserDTO> list = users.stream().map(this::getTeacherDTO).toList();
        return new ApiResponse(list);
    }

    public UserDTO getTeacherDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .fileId(Optional.ofNullable(user.getFile()).map(File::getId).orElse(null))
                .build();
    }

    public ResGroupDto getDto(Group group) {
        return ResGroupDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .build();
    }




//    @Transactional
//    public ApiResponse getOneTeacher(Long teacherId) {
//        User user = userRepository.findById(teacherId).orElse(null);
//        if (user == null) {
//            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
//        }
//        List<Long> categoryIds = new ArrayList<>();
//        for (Category category : user.getCategories()) {
//            categoryIds.add(category.getId());
//        }
//
//        return new ApiResponse(convertUserToTeacherDTO(user,categoryIds));
//    }


    // TODO Ishlatilmay yotgan ekan cament olib quydim agar kerak bulmasa uchirib tashela


}
