package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sfera.edu_platform.entity.Chat;
import uz.sfera.edu_platform.entity.ChatGroup;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.exception.BadRequestException;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ChatDto;
import uz.sfera.edu_platform.payload.ChatGroupDto;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.ResChatGroup;
import uz.sfera.edu_platform.repository.ChatGroupRepository;
import uz.sfera.edu_platform.repository.ChatRepository;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import uz.sfera.edu_platform.security.CurrentUser;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatGroupService {


    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FileRepository fileRepository;


    public ApiResponse groupListForTeacher(@CurrentUser User user) {
        List<ChatGroupDto> chatGroups = chatGroupRepository.getAllByCreatedBy(user.getId())
                .stream().map(chatGroup -> ChatGroupDto.builder()
                        .groupId(chatGroup.getId())
                        .groupName(chatGroup.getGroupName())
                        .groupPhotoId(chatGroup.getFile()!=null ? chatGroup.getFile().getId(): null)
                        .build()).toList();
        return new ApiResponse(chatGroups);
    }

    public ApiResponse groupListForStudent(@CurrentUser User user) {
        List<ChatGroupDto> chatGroups = chatGroupRepository.getAllByStudent(user.getId())
                .stream().map(chatGroup -> ChatGroupDto.builder()
                        .groupId(chatGroup.getId())
                        .groupName(chatGroup.getGroupName())
                        .groupPhotoId(chatGroup.getFile()!=null ? chatGroup.getFile().getId() : null)
                        .build()).toList();
        return new ApiResponse(chatGroups);
    }

    public ApiResponse getChatGroupById(Long groupId) {
        Optional<ChatGroup> chatGroup = chatGroupRepository.findById(groupId);
        if (chatGroup.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("guruh"));
        }
        ChatGroup chatGroupEntity = chatGroup.get();
        ResChatGroup resChatGroup = toResChatGroup(chatGroupEntity);
        return new ApiResponse(resChatGroup);
    }

    @Transactional
    public ApiResponse createChatGroup(String groupName, Set<Long> memberIds, Long fileId) {

        Set<User> members = new HashSet<>();

        if(memberIds !=null){
            for (Long memberId : memberIds) {
                members.add(userRepository.findById(memberId).orElse(null));
            }
        }

        ChatGroup chatGroup = ChatGroup.builder()
                .groupName(groupName)
                .members(members)
                .file(fileRepository.findById(fileId).orElse(null))
                .build();
        chatGroupRepository.save(chatGroup);
        return new ApiResponse("Guruh yaratildi");

    }

    @Transactional
    public ApiResponse deleteChatGroupById(Long groupId, User user) {
        Optional<ChatGroup> chatGroup = chatGroupRepository.findByIdAndCreatedBy(groupId, user.getId());
        if (chatGroup.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("guruh"));
        }
        chatRepository.deleteById(groupId);
        chatGroupRepository.removeMembersFromChatGroup(groupId);
        chatGroupRepository.deleteById(groupId);
        return new ApiResponse("Guruh o'chirildi");
    }

    @Transactional
    public ApiResponse addMembersToGroup(Long groupId, Set<Long> newMemberIds) {
        Optional<ChatGroup> chatGroupOptional = chatGroupRepository.findById(groupId);

        if (chatGroupOptional.isEmpty()) {
            throw new NotFoundException("Чат-группа не найдена");
        }

        ChatGroup chatGroup = chatGroupOptional.get();

        for (Long userId : newMemberIds) {
            if(!chatGroupRepository.existsMemberInChat(chatGroup.getId(), userId)) {
                userRepository.findById(userId).ifPresent(user ->
                        chatGroupRepository.addMemberToChatGroup(chatGroup.getId(), userId));
            }
        }
       chatGroupRepository.save(chatGroup);
        return new ApiResponse("Студент был добавлен в группу");
    }

    @Transactional
    public ApiResponse removeMemberFromGroup(Long groupId, Long userId) {
        Optional<ChatGroup> chatGroupOptional = chatGroupRepository.findById(groupId);

        if (chatGroupOptional.isEmpty()) {
            throw new NotFoundException("Чат-группа не найдена");
        }

        ChatGroup chatGroup = chatGroupOptional.get();
        chatGroupRepository.removeMemberFromChatGroup(chatGroup.getId(), userId);
        return new ApiResponse("Студент исключен из группы");
    }


    @Transactional
    public ChatDto sendMessageToGroup(Long senderId, String content, Long groupId) {
        Optional<ChatGroup> chatGroupOptional = chatGroupRepository.findById(groupId);

        if (chatGroupOptional.isEmpty()) {
            throw new IllegalArgumentException("Чат-группа не найдена");
        }

        ChatGroup chatGroup = chatGroupOptional.get();
        Chat chat = Chat.builder()
                .sender(senderId)
                .content(content)
                .chatGroup(chatGroup)
                .build();

        Chat save = chatRepository.save(chat);
        return toChatDto(save);
    }

    @Transactional
    public ChatDto replyToMessage(Long senderId, String content, Long groupId, Long messageId) {
        Optional<ChatGroup> chatGroupOptional = chatGroupRepository.findById(groupId);

        if (chatGroupOptional.isEmpty()) {
            throw new NotFoundException("Чат-группа не найдена");
        }

        Optional<Chat> replyChatOptional = chatRepository.findById(messageId);
        if (replyChatOptional.isEmpty()) {
            throw new NotFoundException("Сообщение для ответа не найдено");
        }

        ChatGroup chatGroup = chatGroupOptional.get();
        Chat replyChat = replyChatOptional.get();
        Chat chat = Chat.builder()
                .sender(senderId)
                .content(content)
                .chatGroup(chatGroup)
                .build();
        chat.setReplayChat(replyChat);

        return toChatDto(chatRepository.save(chat));
    }


    private ResChatGroup toResChatGroup(ChatGroup group) {
        List<Chat> allByGroupId = chatRepository.findAllByChatGroupId(group.getId());
        List<ChatDto> chatDtos = new ArrayList<>();
        for (Chat chat : allByGroupId) {
            chatDtos.add(toChatDto(chat));
        }
        return ResChatGroup.builder()
                .chats(chatDtos)
                .groupName(group.getGroupName())
                .fileId(group.getFile()!=null ? group.getFile().getId() : null)
                .totalMembers(chatGroupRepository.countAllMembersInChatGroup(group.getId())+1)
                .build();
    }


    private ChatDto toChatDto(Chat chat) {
        User user = userRepository.findById(chat.getSender()).get();
        return ChatDto.builder()
                .id(chat.getId())
                .sender(chat.getSender())
                .content(chat.getContent())
                .createdAt(String.valueOf(chat.getCreatedAt()))
                .isEdited(chat.getIsEdited() != 0)
                .senderImg(user.getFile()!=null ? user.getFile().getId():null)
                .senderName(user.getFullName())
                .build();
    }
}
