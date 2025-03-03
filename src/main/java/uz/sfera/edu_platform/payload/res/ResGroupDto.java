package uz.sfera.edu_platform.payload.res;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResGroupDto {

    private Long groupId;
    private String groupName;
}
