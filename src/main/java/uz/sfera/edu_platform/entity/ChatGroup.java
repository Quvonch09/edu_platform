package uz.sfera.edu_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.sfera.edu_platform.entity.template.AbsEntity;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatGroup extends AbsEntity {

    @Column(nullable = false)
    private String groupName;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> members;

}
