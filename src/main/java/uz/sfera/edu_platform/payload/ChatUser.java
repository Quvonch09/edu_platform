package uz.sfera.edu_platform.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatUser {
    private Long userId;
    private String name;
    private String phone;
    private String status;
    private Long attachmentId;
    private int newMessageCount;
    private ChatDto chatDto;
}
