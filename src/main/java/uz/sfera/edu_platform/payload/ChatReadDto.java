package uz.sfera.edu_platform.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatReadDto {

    private Long chatId;

    private boolean read;

    private Long senderId;

    private Long receiverId;

}
