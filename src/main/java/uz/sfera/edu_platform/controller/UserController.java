package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserPaymentStatus;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.UserDTO;
import uz.sfera.edu_platform.payload.req.ReqAdmin;
import uz.sfera.edu_platform.payload.req.ReqTeacher;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher qushish")
    @PostMapping("/saveTeacher")
    public ResponseEntity<ApiResponse> saveTeacher(@Valid @RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.saveTeacher(reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN' , 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher va adminlarni search qilish")
    @GetMapping("/searchUsers")
    public ResponseEntity<ApiResponse> searchUsers(@RequestParam(required = false, value = "fullName") String fullName,
                                                     @RequestParam(required = false, value = "phoneNumber") String phoneNumber,
                                                     @RequestParam(required = false, value = "categoryId") Long categoryId,
                                                     @RequestParam(value = "role") Role role,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        ApiResponse apiResponse = userService.searchUsers(fullName, phoneNumber, categoryId,role, page, size);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni bittasini kurish")
    @GetMapping("/teacherById/{teacherId}")
    public ResponseEntity<ApiResponse> getOneTeacher(@PathVariable Long teacherId) {
        ApiResponse apiResponse = userService.getOneTeacher(teacherId);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni update qilish")
    @PutMapping("/updateTeacher/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacher(@PathVariable Long teacherId, @Valid @RequestBody ReqTeacher reqTeacher) {
        ApiResponse apiResponse = userService.updateTeacher(teacherId, reqTeacher);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacherni activeni uzgartirish")
    @PutMapping("/updateTeacher/active/{teacherId}")
    public ResponseEntity<ApiResponse> updateTeacherActive(@PathVariable Long teacherId, @RequestParam Boolean active) {
        ApiResponse apiResponse = userService.updateActiveTeacher(teacherId, active);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "CEO/ADMIN teacher/admin delete qilish")
    @DeleteMapping("/deleteTeacher/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId, @CurrentUser User user) {
        ApiResponse apiResponse = userService.deleteTeacher(userId, user);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO admin qushish")
    @PostMapping("/saveAdmin")
    public ResponseEntity<ApiResponse> saveAdmin(@Valid @RequestBody ReqAdmin reqAdmin) {
        ApiResponse apiResponse = userService.saveAdmin(reqAdmin);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO admin bittasini kurish")
    @GetMapping("/adminById/{adminId}")
    public ResponseEntity<ApiResponse> getOneAdmin(@PathVariable Long adminId) {
        ApiResponse apiResponse = userService.getOneAdmin(adminId);
        return ResponseEntity.ok(apiResponse);
    }



    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @Operation(summary = "CEO adminni update qilish")
    @PutMapping("/updateAdmin/{adminId}")
    public ResponseEntity<ApiResponse> updateAdmin(@PathVariable Long adminId, @Valid @RequestBody ReqAdmin reqAdmin) {
        ApiResponse apiResponse = userService.updateAdmin(adminId, reqAdmin);
        return ResponseEntity.ok(apiResponse);
    }





    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Barcha userlarni listini kurish")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getUserList(@RequestParam Role role) {
        ApiResponse apiResponse = userService.getUsersList(role);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Barcha uzini profilini kurish")
    @GetMapping("/getMe")
    public ResponseEntity<ApiResponse> getMe(@CurrentUser User user) {
        ApiResponse apiResponse = userService.getMe(user);
        return ResponseEntity.ok(apiResponse);
    }




    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_CEO')")
    @Operation(summary = "Barcha user uzini profilini update qilish")
    @PutMapping("/updateUser")
    public ResponseEntity<ApiResponse> updateUser(@CurrentUser User user,@Valid @RequestBody UserDTO userDTO) {
        ApiResponse apiResponse = userService.updateUser(user, userDTO);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "Student ning o'zining teacherlari listi")
    @GetMapping("/for-student")
    public ResponseEntity<ApiResponse> getTeachers(@CurrentUser User user){
        ApiResponse apiResponse = userService.getTeacher(user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "ChatId saqlash uchun")
    @PostMapping("/saveChatId")
    public ResponseEntity<ApiResponse> saveChatId(@RequestParam Long chatId, @RequestParam String phoneNumber){
        ApiResponse apiResponse = userService.saveUserChatId(chatId, phoneNumber);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "userlarni check qilish uchun")
    @GetMapping("/checkUser")
    public ResponseEntity<ApiResponse> checkUser(@RequestParam(required = false) UserPaymentStatus paymentStatus) {
        ApiResponse apiResponse = userService.getCheckUsers(paymentStatus);
        return ResponseEntity.ok(apiResponse);
    }

}
