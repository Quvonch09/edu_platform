package uz.sfera.edu_platform.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResUser {
    private Long userId;
    private String fullName;
    private String groupName;
    private Long chatId;
    private boolean status;
}
