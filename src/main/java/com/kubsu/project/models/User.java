package com.kubsu.project.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "k_user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "u_id")
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым!")
    @Size(max = 30, message = "Имя пользователя не может быть больше 10 символов!")
    @Column(name = "u_username")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым!")
    @Size(min = 4, message = "Пароль не может быть меньше 4 символов!")
    @Column(name = "u_password")
    private String password;

    @Column(name = "u_active")
    private boolean active;

    @NotBlank(message = "Почта не может быть пустой!")
    @Email(message = "Введите верный формат электронной почты!")
    @Column(name = "u_email")
    private String email;

    @Column(name = "u_activation_code")
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

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
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}