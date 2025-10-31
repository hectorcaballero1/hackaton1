package org.example.hackaton01.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username es obligatorio")
    @Size(min = 3, max = 30, message = "Username entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "username, contine caracteres alfanuméricos")
    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @NotBlank(message = "Email obligatorio ")
    @Email(message = "Email en formato valido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password es obligatorio")
    @Size(min = 8, message = "Password con minimo 8 caracteres")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column
    private String branch; // null si es CENTRAL, obligatorio si es BRANCH

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // validacion personalizada: Branch obligatorio si es BRANCH
    // validamos el usuario se branch
    @AssertTrue(message = "Branch es obligatorio para usuarios BRANCH")
    private boolean isBranchValid() {
        if (role == UserRole.ROLE_BRANCH) {
            // true si branch no es nulo o vacio
            return branch != null && !branch.trim().isEmpty();
        }
        return true;
    }
    // Callbacks JPA para fechas automáticas, pasamos las fechas
    // fecha automatica al crear
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    // fecha automatica al actualizar
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
        return true;
    }
}
