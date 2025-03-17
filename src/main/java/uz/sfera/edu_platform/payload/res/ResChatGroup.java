package uz.sfera.edu_platform.payload.res;

import lombok.*;
import uz.sfera.edu_platform.payload.ChatDto;
import uz.sfera.edu_platform.payload.ChatGroupDto;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResChatGroup {
    private List<ChatDto> chats;
    private int totalMembers;
    private String groupName;
}
