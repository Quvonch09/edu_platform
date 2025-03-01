package uz.sfera.edu_platform.payload.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqPassTest {

    private Long questionId;

    private Long optionId;
}
