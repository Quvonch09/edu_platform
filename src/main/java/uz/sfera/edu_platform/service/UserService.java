package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.ChatStatus;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserPaymentStatus;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.*;
import uz.sfera.edu_platform.payload.auth.ResponseLogin;
import uz.sfera.edu_platform.payload.req.ReqAdmin;
import uz.sfera.edu_platform.payload.req.ReqTeacher;
import uz.sfera.edu_platform.payload.res.*;
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

        if (userRepository.existsByPhoneNumberAndEnabled(reqTeacher.getPhoneNumber(),true)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu telefon raqam"));
        }

        List<Category> categories = new ArrayList<>();
        List<Long> notFounds = new ArrayList<>();
        for (Long categoryId : reqTeacher.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null){
                notFounds.add(categoryId);
            }
            categories.add(category);
        }


        File file = (reqTeacher.getFileId() != null)
                ? fileRepository.findById(reqTeacher.getFileId()).orElse(null)
                : null;

        User teacher = User.builder()
                .fullName(reqTeacher.getFullName())
                .phoneNumber(reqTeacher.getPhoneNumber())
                .categories(categories)  // Ortacha ro‘yxat yaratamiz
                .password(passwordEncoder.encode(reqTeacher.getPassword()))
                .enabled(true)
                .role(Role.ROLE_TEACHER)
                .file(file)
                .deleted(false)
                .build();

        userRepository.save(teacher);

        if (!notFounds.isEmpty()){
            return new ApiResponse("Kategoriyalar topilmadi: " + notFounds);
        }
        return new ApiResponse("O'qituvchi muvaffaqiyatli saqlandi");
    }


    public ApiResponse searchUsers(String fullName, String phoneNumber, Long categoryId, Role role, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<User> allTeachers = userRepository.searchUsers(fullName, phoneNumber, categoryId,
                role != null ? role.name() : null, pageRequest);

        List<TeacherDTO> teacherList = allTeachers.stream()
                .map(user -> convertUserToTeacherDTO(user,
                        categoryRepository.findAllByActiveAndTeacherId((byte) 1, user.getId()).stream()
                                .map(category -> new ResCategory(category.getId(), category.getName()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        if (teacherList.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Userlar"));
        }

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
                .active(user.isEnabled())
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
        List<User> users = (role != null) ? userRepository.findAllByRoleAndDeletedFalse(role) : userRepository.findAll();

        List<UserDTO> userDTOList = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        if (userDTOList.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Userlar"));
        }

        return new ApiResponse(userDTOList);
    }

    public ApiResponse getOneTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null || user.isDeleted()) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

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


    @Transactional
    public ApiResponse updateTeacher(Long teacherId, ReqTeacher reqTeacher) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null){
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (userRepository.existsByPhoneNumberAndEnabledIsTrueAndIdNot(reqTeacher.getPhoneNumber(),teacherId)){
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu telefon nomer"));
        }

        user.setFullName(reqTeacher.getFullName());
        user.setPhoneNumber(reqTeacher.getPhoneNumber());

        File file = (reqTeacher.getFileId() != null)
                ? fileRepository.findById(reqTeacher.getFileId()).orElse(null)
                : null;

        List<Category> categories = new ArrayList<>();
        for (Long categoryId : reqTeacher.getCategoryIds()) {
            categories.add(categoryRepository.findById(categoryId).orElse(null));
        }

        user.setFile(file);
        user.setCategories(categories);

        if (reqTeacher.getPassword() != null && !reqTeacher.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(reqTeacher.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(reqTeacher.getPassword()));
            }
        }


        userRepository.save(user);
        return new ApiResponse("Successfully updated teacher");
    }


    public ApiResponse updateActiveTeacher(Long teacherId, Boolean active) {
        User user = userRepository.findById(teacherId).orElse(null);
        if (user == null){
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        user.setEnabled(active);
        userRepository.save(user);

        return new ApiResponse("Teacher successfully updated");
    }


    public ApiResponse deleteTeacher(Long userId, User currentUser) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null){
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (user.getRole().equals(Role.ROLE_ADMIN) && !currentUser.getRole().equals(Role.ROLE_CEO)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Sizda adminni o‘chirishga huquq yo‘q"));
        }

        user.setDeleted(true);
        user.setPhoneNumber(user.getPhoneNumber() + "_" + UUID.randomUUID().toString().substring(0, 8) + "_deleted");

        userRepository.save(user);
        return new ApiResponse("User successfully deleted");
    }


    public ApiResponse saveAdmin(ReqAdmin reqAdmin){
        if (userRepository.existsByPhoneNumberAndEnabled(reqAdmin.getPhoneNumber(), true)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu Admin"));
        }

        User admin = User.builder()
                .fullName(reqAdmin.getFullName())
                .phoneNumber(reqAdmin.getPhoneNumber())
                .role(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode(reqAdmin.getPassword()))
                .file(reqAdmin.getFileId() != null ? fileRepository.findById(reqAdmin.getFileId()).orElse(null) : null)
                .enabled(true)
                .deleted(false)
                .build();

        userRepository.save(admin);
        return new ApiResponse("Admin successfully saved");
    }

    public ApiResponse getOneAdmin(Long adminId){
        User user = userRepository.findById(adminId).orElse(null);
        if (user == null || user.isDeleted()){
            return new ApiResponse(ResponseError.NOTFOUND("Admin"));
        }

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
        User user = userRepository.findById(adminId).orElse(null);
        if (user == null){
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        user.setFullName(reqAdmin.getFullName());
        user.setPhoneNumber(reqAdmin.getPhoneNumber());

        if (reqAdmin.getPassword() != null && !reqAdmin.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(reqAdmin.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(reqAdmin.getPassword()));
            }
        }



        userRepository.save(user);
        return new ApiResponse("Successfully updated admin");
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
        boolean phoneChanged = !user.getPhoneNumber().equals(userDTO.getPhoneNumber()); // Telefon raqam o'zgarganligini tekshiramiz

        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        }

        if (userDTO.getFileId() == null) {
            user.setFile(null);
        } else {
            user.setFile(fileRepository.findById(userDTO.getFileId()).orElse(null));
        }

        userRepository.save(user);

        if (phoneChanged) {
            String token = jwtProvider.generateToken(user.getPhoneNumber());
            ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
            return new ApiResponse(responseLogin);
        }

        return new ApiResponse("User successfully updated");
    }


    public ApiResponse checkUser(String parentPhoneNumber){

        User user = userRepository.findByPhoneOrParentPhoneNumber(parentPhoneNumber);
        if (user == null || user.getChatId() == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        Group group = groupRepository.findByStudentId(user.getId()).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }


        ResUser resUser;

        if (user.getPhoneNumber().equals(parentPhoneNumber)){
            resUser = resUser(user, group.getName());
        } else {
            resUser = resUser(user, group.getName());
        }

        return new ApiResponse(resUser);
    }


    public ApiResponse getCheckUsers(UserPaymentStatus paymentStatus){
        Page<ResStudent> users = null;

        if (paymentStatus == null){
            users = userRepository.searchStudents(null, null, null, null,
                    null, null, null, null, PageRequest.of(0, 1000)
            );
        } else if (paymentStatus.equals(UserPaymentStatus.TULOV_QILGAN)){
            users = userRepository.searchStudents(
                    null, null, null, null,
                    null, null, null, true, PageRequest.of(0, 10000)
            );
        } else if (paymentStatus.equals(UserPaymentStatus.TULOV_QILMAGAN)){
            users = userRepository.searchStudents(
                    null, null, null, null,
                    null, null, null, false, PageRequest.of(0, 1000)
            );
        }


        assert users != null;
        if (users.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        List<ResUser> resUsers = new ArrayList<>();

        for (ResStudent resStudent : users.getContent()) {
            ResUser resUser = ResUser.builder()
                    .userId(resStudent.getId())
                    .groupName(resStudent.getGroupName())
                    .fullName(resStudent.getFullName())
                    .chatId(resStudent.getChatId())
                    .build();
            resUsers.add(resUser);
        }

        return new ApiResponse(resUsers);
    }

    public ApiResponse saveUserChatId(Long chatId, String phoneNumber){
        User user = userRepository.findByParentPhoneNumber(phoneNumber);
        if (user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Bu nomerdagi user topilmadi"));
        }

        user.setChatId(chatId);
        userRepository.save(user);
        return new ApiResponse("User successfully saved");
    }


    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);

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


    private ResUser resUser(User user,String groupName) {
        return ResUser.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .groupName(groupName)
                .chatId(user.getChatId())
                .build();
    }
}
