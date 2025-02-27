package com.example.edu_platform.controller;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.*;
import com.example.edu_platform.security.CurrentUser;
import com.example.edu_platform.service.ChatService;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;


    @Operation(summary = "Userlar chatda yozishgan userlarni chiqaradi")
    @GetMapping("/users")
    public ResponseEntity<List<ChatUser>> getChatUsers(@CurrentUser User user)
    {
        return ResponseEntity.ok(chatService.getChatUsers(user));
    }


//    @Operation(summary = "Начать чат Web uchun. Status MASTER, CLIENT")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_СEO')")
//    @GetMapping("/web/nachat-chat")
//    public ResponseEntity<List<SearchChatUser>> getChatNachat(
//            @RequestParam("status") String status,
//            @RequestParam(value = "fullName",required = false) String fullName,
//            @RequestParam(value = "phone",required = false) String phone)
//    {
//        return ResponseEntity.ok(chatService.searchNachatChat(fullName,phone,status));
//    }

//
//    @Operation(summary = "Web uchun. Barcha messagelarni o'qildi qilish.")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
//    @GetMapping("/all-message-ready")
//    public ResponseEntity<ApiResponse> getAllMessagesReady(@CurrentUser User user)
//    {
//        return ResponseEntity.ok(chatService.makeAllChatMessagesRead(user));
//    }


    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER')")
    @PostMapping("/nachat-chat/send")
    public ResponseEntity<ApiResponse> sendChatMessage(
            @RequestBody SendNachatChat sendNachatChat, @CurrentUser User user)
    {
        return ResponseEntity.ok(chatService.sendNachatChat(sendNachatChat,user));
    }


    @Operation(summary = "Chatda userlarni search qilish. Faqat user o'zi yozishgan userlarni chiqaradi")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_STUDENT')")
    @GetMapping("/search")
    public ResponseEntity<List<SearchChatUser>> search(@CurrentUser User user, @RequestParam(value = "name") String name)
    {
        return ResponseEntity.ok(chatService.search(user, name));
    }


    @Operation(summary = "Userni chatda online yoki offlini bo'lishini saqlash")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_STUDENT')")
    @GetMapping("/online-offline")
    public ResponseEntity<ApiResponse> onlineOffline(
            @RequestParam(value = "isActive") boolean isActive,
            @CurrentUser User user)
    {
        return ResponseEntity.ok(chatService.onlineOffline(isActive, user));
    }


    @MessageMapping("/chat")
    @SendTo("/user")
    public void processMessage(@Payload ChatDto chatDto)
    {
        log.info("controller ga keldi");

        ChatDto saveChat = chatService.saveMessage(chatDto);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatDto.getSender()),
                "/queue/messages",
                saveChat
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatDto.getReceiver()),
                "/queue/messages",
                saveChat
        );
    }


    @MessageMapping("/isRead")
    public void isReadMessage(@Payload ChatIds chatIds)
    {
        List<ChatReadDto> dtoList = chatService.isReadMessage(chatIds);

        if (!dtoList.isEmpty()){
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(dtoList.get(0).getSenderId()),
                    "/queue/messages",
                    dtoList
            );

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(dtoList.get(0).getReceiverId()),
                    "/queue/messages",
                    dtoList
            );
        }
    }

    @MessageMapping("/replay")
    public void forward(@Payload ChatMessageEditOrReplay edit)
    {
        ChatDto forward = chatService.replay(edit);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(edit.chatDto().getReceiver()),
                "/queue/messages",
                forward
        );
    }


    @MessageMapping("/editMessage")
    public void editMessage(@Payload ChatMessageEditOrReplay edit)
    {
        ChatDto chatDto1 = chatService.editMessage(edit);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(edit.chatDto().getSender()),
                "/queue/messages",
                chatDto1
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(edit.chatDto().getReceiver()),
                "/queue/messages",
                chatDto1
        );
    }


    @MessageMapping("/deleteMessage")
    public void deleteMessage(@Payload Long chatId)
    {
        chatService.deleteMessage(chatId);
    }


    @MessageMapping("/deleteMessage/list")
    public void deleteMessageList(@Payload ChatIds chatIds)
    {
        chatService.deleteMessageList(chatIds);
    }


    @Operation(summary = "User chatda tanlagan user bilan chatda yozishgan xabarlari chiqadi")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER')")
    @GetMapping("/messages/{sender}/{recipient}")
    public ResponseEntity<List<ChatDto>> findChatMessages(
            @PathVariable Long sender,
            @PathVariable Long recipient)
    {
        return ResponseEntity.ok(chatService.findChatMessages(sender, recipient));
    }

    @Operation(summary = "User o'zi yozishgan uzerlarni o'chiradi")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_TEACHER')")
    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse> deleteUser(
            @RequestParam(value = "usersIds") List<Long> usersIds,
            @CurrentUser User user)
    {
        return ResponseEntity.ok(chatService.deleteUser(usersIds, user));
    }
}
