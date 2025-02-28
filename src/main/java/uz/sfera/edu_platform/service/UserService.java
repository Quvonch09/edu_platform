package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.ChatStatus;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.*;
import uz.sfera.edu_platform.payload.auth.ResponseLogin;
import uz.sfera.edu_platform.payload.req.ReqAdmin;
import uz.sfera.edu_platform.payload.req.ReqTeacher;
import uz.sfera.edu_platform.payload.res.ResCategory;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResStudentCount;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import uz.sfera.edu_platform.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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


    //    Teacher CRUD
    public ApiResponse saveTeacher(ReqTeacher reqTeacher) {
        if (reqTeacher == null || reqTeacher.getFullName().isEmpty()) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Iltimos ma'lumot kiriting"));
        }

        if (userRepository.existsByPhoneNumberAndRoleAndEnabledTrue(reqTeacher.getPhoneNumber(), Role.ROLE_TEACHER)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu User"));
        }

        List<Category> categoryList = new ArrayList<>();

        Category category = categoryRepository.findById(reqTeacher.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }
        categoryList.add(category);

        User teacher = User.builder()
                .fullName(reqTeacher.getFullName())
                .phoneNumber(reqTeacher.getPhoneNumber())
                .categories(List.of(category))
                .password(passwordEncoder.encode(reqTeacher.getPassword()))
                .enabled(true)
                .role(Role.ROLE_TEACHER)
                .file(fileRepository.findById(reqTeacher.getFileId()).orElse(null))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(teacher);
        return new ApiResponse("Teacher successfully saved");
    }



    @Transactional
    public ApiResponse searchUsers(String fullName, String phoneNumber, Long groupId, Role role, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> allTeachers = userRepository.searchUsers(fullName, phoneNumber, groupId, role.name(), pageRequest);

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



    public ApiResponse getUsersList(Role role) {
        List<UserDTO> userDTOList = userRepository.findAllByRole(role).stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole().name())
                        .fileId(user.getFile() != null ? user.getFile().getId() : null)
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse(userDTOList);
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


    public ApiResponse getOneTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }
        List<ResStudentCount> res = groupRepository.findAllStudentsByTeacherId(teacherId);

        return new ApiResponse(res); // Bo'sh bo'lsa, oddiy bo'sh list qaytaradi
    }





    public ApiResponse updateTeacher(Long teacherId, ReqTeacher reqTeacher) {

        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        user.setFullName(reqTeacher.getFullName());
        user.setPhoneNumber(reqTeacher.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(reqTeacher.getPassword())); // Parolni kodlash
        user.setFile(fileRepository.findById(reqTeacher.getFileId()).orElse(null));

        userRepository.save(user);
        return new ApiResponse("Teacher successfully updated");
    }



    public ApiResponse updateActiveTeacher(Long teacherId, Boolean active) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }
        user.setEnabled(active);
        userRepository.save(user);
        return new ApiResponse("Teacher successfully updated");
    }



    public ApiResponse deleteTeacher(Long userId, User currentUser) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        if (user.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getRole().equals(Role.ROLE_CEO)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Sizda adminni o‘chirishga huquq yo‘q"));
        }

        user.setEnabled(false);
        userRepository.save(user);
        return new ApiResponse("User successfully deleted");
    }



    //    Admin CRUD
    public ApiResponse saveAdmin(ReqAdmin reqAdmin){
        boolean b = userRepository.existsByPhoneNumberAndRoleAndEnabledTrue(reqAdmin.getPhoneNumber(), Role.ROLE_ADMIN);
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu Admin"));
        }

        User admin = User.builder()
                .fullName(reqAdmin.getFullName())
                .phoneNumber(reqAdmin.getPhoneNumber())
                .role(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode(reqAdmin.getPassword()))
                .file(fileRepository.findById(reqAdmin.getFileId()).orElse(null))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(admin);
        return new ApiResponse("Admin successfully saved");
    }


    public ApiResponse getOneAdmin(Long adminId){
        User user = userRepository.findById(adminId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Admin"));
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
        User user = userRepository.findById(adminId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Admin"));
        }

        user.setFullName(reqAdmin.getFullName());
        user.setPhoneNumber(reqAdmin.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(reqAdmin.getPassword()));
        userRepository.save(user);
        return new ApiResponse("Admin successfully updated");
    }


    public ApiResponse getMe(User user){
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
        return new ApiResponse(userDTO);
    }


    public ApiResponse updateUser(User user,UserDTO userDTO) {
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setFile(fileRepository.findById(userDTO.getFileId()).orElse(null));
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getPhoneNumber());
        ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
        return new ApiResponse(responseLogin);
    }


    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public void onlineOffline(User user, boolean isActive) {
        user.setChatStatus(isActive ? ChatStatus.ONLINE : ChatStatus.OFFLINE);
        userRepository.save(user);
    }


    public List<User> searchForChat(String fullName, String phone, String roleName) {
        return userRepository.searchForChat(fullName, phone, roleName);
    }

    private TeacherDTO convertUserToTeacherDTO(User user, List<ResCategory> categoryIds) {
        Group group = groupRepository.findGroup(user.getId());

        return TeacherDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .categories(categoryIds)
                .active(user.isEnabled())
                .groupCount(groupRepository.countByTeacherId(user.getId()))
                .groupId(group != null ? group.getId() : null)
                .groupName(group != null ? group.getName() : null)
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
    }

    public ApiResponse getTeacher (User user){

        List<User> users = userRepository.searchForTeacher(user.getId());
        List<UserDTO> list = users.stream().map(this::getTeacherDTO).toList();
        return new ApiResponse(list);
    }


    public UserDTO getTeacherDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .fileId(user.getFile() != null ? user.getFile().getId() : null)
                .build();
    }

}
