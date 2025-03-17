package uz.sfera.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.entity.ChatGroup;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ChatDto;
import uz.sfera.edu_platform.payload.ChatMessageEditOrReplay;
import uz.sfera.edu_platform.security.CurrentUser;
import uz.sfera.edu_platform.service.ChatGroupService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/chat/group")
@RequiredArgsConstructor
public class ChatGroupController {


    private final ChatGroupService chatGroupService;
    private final SimpMessagingTemplate messagingTemplate;


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Teacher o'zini yaratgan gruppalarini ko'ra oladi")
    @GetMapping("/list/teacher")
    public ResponseEntity<ApiResponse> getAllChatGroups(@CurrentUser User user) {
        return ResponseEntity.ok(chatGroupService.groupListForTeacher(user));
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "Student o'zi azo bolgan guruhlarini ko'ra oladi")
    @GetMapping("/list/student")
    public ResponseEntity<ApiResponse> getAllChatGroupsForStudent(@CurrentUser User user) {
        return ResponseEntity.ok(chatGroupService.groupListForStudent(user));
    }

    @Operation(summary = "guruhni ichidagi chatlarni korish Teacher ham Student ham kora oladi")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> getChatGroupById(@PathVariable Long groupId) {
        ApiResponse chatGroupById = chatGroupService.getChatGroupById(groupId);
        return ResponseEntity.ok(chatGroupById);
    }


    @MessageMapping("/send/group")
    public void sendMessage(@Payload ChatDto message) {
        Long groupId = message.getGroup();
        System.out.println(groupId);
        ChatDto chatDto = chatGroupService.sendMessageToGroup(message.getSender(), message.getContent(), groupId);
        log.info("Sent message");
        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,
                chatDto
        );
    }

    // Ответить на сообщение в группе
    @MessageMapping("/reply/group")
    public void replyToMessage(@Payload ChatMessageEditOrReplay message) {
        Long groupId = message.chatDto().getGroup();
        ChatDto chatDto = chatGroupService.replyToMessage(message.chatDto().getSender(), message.chatDto().getContent(),
                groupId, message.messageId());

        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,  // Путь темы, на который подписываются участники группы
                chatDto                         // Сообщение, которое отправляется группе
        );
    }




//    @PreAuthorize("hasRole('ROLE_TEACHER')")
//    @PutMapping("/deleteMember/{groupId}")
//    public ResponseEntity<ApiResponse> deleteMember(@PathVariable Long groupId, @RequestParam Long memberId) {
//        return ResponseEntity.ok(chatGroupService.removeMemberFromGroup(groupId, memberId));
//    }
}
