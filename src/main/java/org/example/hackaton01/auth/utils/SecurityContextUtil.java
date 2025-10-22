package org.example.hackaton01.auth.utils;

import org.example.hackaton01.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

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
    public String getCurrentUserId() {
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
        return "CENTRAL".equals(getCurrentRole());
    }

    /**
     * Verifica si el usuario actual tiene el role BRANCH
     * @return true si es BRANCH, false si no
     */
    public boolean isBranchRole() {
        return "BRANCH".equals(getCurrentRole());
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
