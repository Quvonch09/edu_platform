package uz.sfera.edu_platform.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestGroupDTO {
    private Long id;
    private String name;
    private boolean active;
    private int studentCount;
    private LocalDate createdAt;
}
