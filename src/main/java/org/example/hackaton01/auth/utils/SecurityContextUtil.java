package org.example.hackaton01.auth.utils;

import org.example.hackaton01.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {
    public String getCurrentUserRole() {
        // Obtener el Authentication del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Asumiendo que tu UserDetails implementación tiene un método getRole()
            // Depende de cómo hayas implementado la autenticación.
            // Por ejemplo, si tu UserDetails implementación es UserDetails, quizás no tengas el rol.
            // Si usas una clase custom, como UserPrincipal, entonces puedes hacer:
            // return ((UserPrincipal) authentication.getPrincipal()).getRole();
        }
        return null;
    }

    public String getCurrentUserBranch() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Similar a getCurrentUserRole, pero para la sucursal.
            // return ((UserPrincipal) authentication.getPrincipal()).getBranch();
        }
        return null;
    }


    /**
     * Obtiene el usuario autenticado del SecurityContext
     * @return User autenticado
     * @throws RuntimeException si no hay usuario autenticado
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new RuntimeException("Invalid authentication principal");
    }

    /**
     * Obtiene el username del usuario autenticado
     * @return username
     */
    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * Obtiene el email del usuario autenticado
     * @return email
     */
    public String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    /**
     * Obtiene el ID del usuario autenticado
     * @return user ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Obtiene el role del usuario autenticado
     * @return role (CENTRAL o BRANCH)
     */
    public String getCurrentRole() {
        return getCurrentUser().getRole().name();
    }

    /**
     * Obtiene la sucursal del usuario autenticado (puede ser null si es CENTRAL)
     * @return branch name o null
     */
    public String getCurrentBranch() {
        return getCurrentUser().getBranch();
    }

    /**
     * Verifica si el usuario actual tiene el role CENTRAL
     * @return true si es CENTRAL, false si no
     */
    public boolean isCentralRole() {
        String role = getCurrentRole();
        return "ROLE_CENTRAL".equals(role) || "CENTRAL".equals(role);
    }

    /**
     * Verifica si el usuario actual tiene el role BRANCH
     * @return true si es BRANCH, false si no
     */
    public boolean isBranchRole() {
        String role = getCurrentRole();
        return "ROLE_BRANCH".equals(role) || "BRANCH".equals(role);
    }

    /**
     * Valida que un usuario BRANCH solo acceda a su propia sucursal
     * @param branch la sucursal que se intenta acceder
     * @throws RuntimeException si el usuario BRANCH intenta acceder a otra sucursal
     */
    public void validateBranchAccess(String branch) {
        if (isBranchRole() && !getCurrentBranch().equals(branch)) {
            throw new RuntimeException("Access denied: You can only access data from your own branch");
        }
    }
}
