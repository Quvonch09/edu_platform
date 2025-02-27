package uz.sfera.edu_platform.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResPayment {
    private Integer countAllStudent;
    private Integer tulovQilganStudent;
    private Integer tulovQilmaganStudent;
    private Double tushum;
    private Double chiqim;
}
