package uz.sfera.edu_platform.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatGroupDto {
    private Long groupId;
    private String groupName;
}
