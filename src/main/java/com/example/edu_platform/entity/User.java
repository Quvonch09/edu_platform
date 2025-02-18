package com.example.edu_platform.entity;

import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.entity.enums.UserStatus;
import com.example.edu_platform.entity.template.AbsEntity;
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

    private String fullName;
    private String phoneNumber;
    private Integer age;
    private String parentPhoneNumber;
    private String password;
    @ManyToMany
    private List<Category> categories;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private LocalDate departure_date;
    private String departure_description;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    private File file;


    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
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
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
