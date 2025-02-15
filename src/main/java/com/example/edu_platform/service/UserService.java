package com.example.edu_platform.service;

import com.example.edu_platform.entity.Category;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.TeacherDTO;
import com.example.edu_platform.payload.req.ReqTeacher;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
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

    public ApiResponse saveTeacher(ReqTeacher reqTeacher) {
        boolean b = userRepository.existsByPhoneNumberAndFullName(reqTeacher.getPhoneNumber(), reqTeacher.getFullName());
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
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(teacher);
        return new ApiResponse("Teacher successfully saved");
    }


    public ApiResponse searchTeacher(String fullName, String phoneNumber, Long groupId, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> allTeachers = userRepository.getAllTeachers(fullName, phoneNumber, groupId, pageRequest);
        List<TeacherDTO> teacherList = new ArrayList<>();
        for (User allTeacher : allTeachers) {
            List<Long> categoryIds = new ArrayList<>();
            for (Category category : allTeacher.getCategories()) {
                categoryIds.add(category.getId());
            }
            teacherList.add(convertUserToTeacherDTO(allTeacher,categoryIds));
        }
        return new ApiResponse(teacherList);
    }


    @Transactional
    public ApiResponse getOneTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : user.getCategories()) {
            categoryIds.add(category.getId());
        }

        return new ApiResponse(convertUserToTeacherDTO(user,categoryIds));
    }

    public ApiResponse updateTeacher(Long teacherId, ReqTeacher reqTeacher) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        user.setFullName(reqTeacher.getFullName());
        user.setPhoneNumber(reqTeacher.getPhoneNumber());
        user.setPassword(reqTeacher.getPassword());
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



    public ApiResponse deleteTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }
        userRepository.delete(user);
        return new ApiResponse("Teacher successfully deleted");
    }




    private TeacherDTO convertUserToTeacherDTO(User user, List<Long> categoryIds) {

        return TeacherDTO.builder()
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .categoryId(categoryIds)
                .active(user.isEnabled())
                .groupCount(groupRepository.countByTeacherId(user.getId()))
                .build();
    }
}
