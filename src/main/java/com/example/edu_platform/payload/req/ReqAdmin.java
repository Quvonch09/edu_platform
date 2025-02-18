package com.example.edu_platform.payload.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqAdmin {
    @Schema(hidden = true)
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String password;
    private Long fileId;
}
