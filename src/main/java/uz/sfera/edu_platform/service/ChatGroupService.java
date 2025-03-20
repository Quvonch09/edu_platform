package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sfera.edu_platform.entity.Chat;
import uz.sfera.edu_platform.entity.ChatGroup;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatGroupService {


    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FileRepository fileRepository;


    public ApiResponse groupListForTeacher(User user) {
        List<ChatGroupDto> chatGroups = chatGroupRepository.getAllByTeacherId(user.getId())
                .stream().map(chatGroup -> ChatGroupDto.builder()
                        .groupId(chatGroup.getId())
                        .groupName(chatGroup.getGroupName())
                        .build()).toList();
        return new ApiResponse(chatGroups);
    }

    public ApiResponse groupListForStudent(User user) {
        List<ChatGroupDto> chatGroups = chatGroupRepository.getAllByStudent(user.getId())
                .stream().map(chatGroup -> ChatGroupDto.builder()
                        .groupId(chatGroup.getId())
                        .groupName(chatGroup.getGroupName())
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


    public void createChatGroup(User teacher, Group group) {
        ChatGroup chatGroup = ChatGroup.builder()
                .groupName(group.getName())
                .group(group)
                .teacher(teacher)
                .build();
        chatGroupRepository.save(chatGroup);
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
    public void addMembersToGroup(ChatGroup chatGroup, Long memberId) {
        if (!chatGroupRepository.existsMemberInChat(chatGroup.getId(), memberId)) {
            chatGroupRepository.addMemberToChatGroup(chatGroup.getId(), memberId);
        }
    }


    public void removeMemberFromGroup(Long chatGroupId, Long userId) {
        chatGroupRepository.removeMemberFromChatGroup(chatGroupId, userId);
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
        Chat save = chatRepository.save(chat);
        return toChatDto(save);

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
                .totalMembers(chatGroupRepository.countAllMembersInChatGroup(group.getId()) + 1)
                .build();
    }


    private ChatDto toChatDto(Chat chat) {
        User user = userRepository.findById(chat.getSender()).get();
        return ChatDto.builder()
                .id(chat.getId())
                .sender(chat.getSender())
                .group(chat.getChatGroup()!=null ? chat.getChatGroup().getId(): null)
                .content(chat.getContent())
                .createdAt(String.valueOf(chat.getCreatedAt()))
                .isEdited(chat.getIsEdited() != 0)
                .senderImg(user.getFile() != null ? user.getFile().getId() : null)
                .senderName(user.getFullName())
                .build();
    }
}
