package com.example.edu_platform.service;

import com.example.edu_platform.entity.Category;
import com.example.edu_platform.entity.Group;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.*;
import com.example.edu_platform.payload.auth.ResponseLogin;
import com.example.edu_platform.payload.req.ReqAdmin;
import com.example.edu_platform.payload.req.ReqTeacher;
import com.example.edu_platform.payload.res.ResCategory;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.payload.res.ResStudentCount;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.FileRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
import com.example.edu_platform.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        boolean b = userRepository
                .existsByPhoneNumberAndRoleAndEnabledTrue(reqTeacher.getPhoneNumber(), Role.ROLE_TEACHER);
        if (b) {
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
                .categories(categoryList)
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
    public ApiResponse searchUsers(String fullName, String phoneNumber, Long groupId,Role role, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> allTeachers = userRepository.searchUsers(fullName, phoneNumber, groupId, role.name(), pageRequest);
        List<TeacherDTO> teacherList = new ArrayList<>();
        for (User allTeacher : allTeachers) {
            List<ResCategory> categories = new ArrayList<>();
            for (Category category : allTeacher.getCategories()) {
                categories.add(new ResCategory(category.getId(), category.getName()));
            }
            teacherList.add(convertUserToTeacherDTO(allTeacher,categories));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(allTeachers.getTotalElements())
                .totalElements(allTeachers.getTotalElements())
                .body(teacherList)
                .build();
        return new ApiResponse(resPageable);
    }


    public ApiResponse getUsersList(Role role){
        List<User> allByRole = userRepository.findAllByRole(role);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : allByRole) {
            UserDTO userDTO = UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole().name())
                    .fileId(user.getFile() != null ? user.getFile().getId() : null)
                    .build();
            userDTOList.add(userDTO);
        }
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
        if (res.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        return new ApiResponse(res);
    }




    public ApiResponse updateTeacher(Long teacherId, ReqTeacher reqTeacher) {
        boolean b = userRepository.existsByPhoneNumberAndRoleAndEnabledTrue(reqTeacher.getPhoneNumber(), Role.ROLE_TEACHER);
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu User"));
        }

        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        user.setFullName(reqTeacher.getFullName());
        user.setPhoneNumber(reqTeacher.getPhoneNumber());
        user.setPassword(reqTeacher.getPassword());
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

        if (user.getRole().equals(Role.ROLE_ADMIN)){
            if (currentUser.getRole().equals(Role.ROLE_CEO)){
                userRepository.delete(user);
            } else {
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Sizda adminni uchirishga huquq yo'q"));
            }
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
}
