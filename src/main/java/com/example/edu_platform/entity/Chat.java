package com.example.edu_platform.entity;

import com.example.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat extends AbsEntity {

    @Column(nullable = false)
    private Long sender;

    @Column(nullable = false)
    private Long receiver;

    @Column(columnDefinition = "text")
    private String content;

    private byte isRead;

    private byte isEdited;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> attachmentIds;

    @ManyToOne
    private Chat replayChat;
}
