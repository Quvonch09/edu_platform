package uz.sfera.edu_platform.payload.res;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResModule {

    private Long id;

    private String name;

    private Long categoryId;

    private boolean isOpen;
}
