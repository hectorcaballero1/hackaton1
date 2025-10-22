package org.example.hackaton01.auth.domain;

import lombok.RequiredArgsConstructor;
import org.example.hackaton01.auth.dto.AuthResponse;
import org.example.hackaton01.auth.dto.LoginRequest;
import org.example.hackaton01.auth.dto.RegisterRequest;
import org.example.hackaton01.auth.utils.JwtUtil;
import org.example.hackaton01.user.domain.Role;
import org.example.hackaton01.user.domain.User;
import org.example.hackaton01.user.dto.UserResponse;
import org.example.hackaton01.user.infrastructure.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public UserResponse register(RegisterRequest request) {
        // Validar que el username no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validar que si el role es BRANCH, debe tener branch
        Role role = Role.valueOf(request.getRole());
        if (role == Role.BRANCH && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new RuntimeException("Branch is required for BRANCH role");
        }

        // Validar que si el role es CENTRAL, branch debe ser null
        if (role == Role.CENTRAL && request.getBranch() != null) {
            throw new RuntimeException("Branch must be null for CENTRAL role");
        }

        // Crear el usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setBranch(role == Role.BRANCH ? request.getBranch() : null);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Obtener el usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generar el token
        String token = jwtUtil.generateToken(
                user,
                user.getRole().name(),
                user.getBranch()
        );

        // Retornar la respuesta
        return new AuthResponse(
                token,
                jwtUtil.getExpirationTime(),
                user.getRole().name(),
                user.getBranch()
        );
    }
}
