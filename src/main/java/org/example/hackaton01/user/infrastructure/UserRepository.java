package org.example.hackaton01.user.infrastructure;

import org.example.hackaton01.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // para login(auth)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    //para validar que no exista el nombre dduplicado y sea unico
    boolean existsByUsername(String username);
    // para validar que no exista el email duplicado
    boolean existsByEmail(String email);
}
