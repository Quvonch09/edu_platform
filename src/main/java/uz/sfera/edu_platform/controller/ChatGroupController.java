package uz.sfera.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Faqat teacher guruh yarata oladi")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createChatGroup(@RequestParam String groupName,
                                                       @RequestParam(required = false) Set<Long> memberIds,
                                                       @RequestParam Long fileId) {
        return ResponseEntity.ok(chatGroupService.createChatGroup(groupName, memberIds, fileId));
    }

    @Operation(summary = "guruhni ichidagi chatlarni korish Teacher ham Student ham kora oladi")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> getChatGroupById(@PathVariable Long groupId) {
        ApiResponse chatGroupById = chatGroupService.getChatGroupById(groupId);
        return ResponseEntity.ok(chatGroupById);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Teacher o'zini guruhlarini delete qiladi")
    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<ApiResponse> deleteChatGroupById(@CurrentUser User user,
                                                           @PathVariable Long groupId) {
        ApiResponse apiResponse = chatGroupService.deleteChatGroupById(groupId, user);
        return ResponseEntity.ok(apiResponse);
    }

    @MessageMapping("/send/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatDto sendMessage(@PathVariable Long groupId, @Payload ChatDto message) {

        ChatDto chatDto = chatGroupService.sendMessageToGroup(message.getSender(), message.getContent(), groupId);

        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,
                chatDto
        );
        return chatDto;
    }

    // Ответить на сообщение в группе
    @MessageMapping("/reply/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatDto replyToMessage(@PathVariable Long groupId, @Payload ChatMessageEditOrReplay message) {
        // Отправляем ответ на сообщение
        ChatDto chatDto = chatGroupService.replyToMessage(message.chatDto().getSender(), message.chatDto().getContent(),
                groupId, message.messageId());

        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,  // Путь темы, на который подписываются участники группы
                chatDto                         // Сообщение, которое отправляется группе
        );
        return chatDto;
    }

    // Добавление участника в группу
    @MessageMapping("/addMember/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatGroup addMember(@PathVariable Long groupId, @Payload Set<Long> newMemberIds) {
        return chatGroupService.addMembersToGroup(groupId, newMemberIds);
    }

    @MessageMapping("/deleteMember/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatGroup deleteMember(@PathVariable Long groupId, @Payload Long memberId) {
        return chatGroupService.removeMemberFromGroup(groupId, memberId);
    }
}
