package uz.sfera.edu_platform.entity;

import uz.sfera.edu_platform.entity.enums.ChatStatus;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.entity.enums.UserStatus;
import uz.sfera.edu_platform.entity.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Users")
public class User extends AbsEntity implements UserDetails {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private Integer age;

    private String parentPhoneNumber;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Category> categories;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private LocalDate departure_date;

    private String departure_description;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    private File file;

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus;

    private boolean deleted;

    private boolean enabled = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
