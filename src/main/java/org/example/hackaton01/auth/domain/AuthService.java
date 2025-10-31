package org.example.hackaton01.auth.domain;

import lombok.RequiredArgsConstructor;
import org.example.hackaton01.auth.dto.AuthResponse;
import org.example.hackaton01.auth.dto.LoginRequest;
import org.example.hackaton01.auth.dto.RegisterRequest;
import org.example.hackaton01.auth.events.UserRegisteredEvent;
import org.example.hackaton01.auth.utils.JwtUtil;
import org.example.hackaton01.user.domain.User;
import org.example.hackaton01.user.domain.UserRole;
import org.example.hackaton01.user.dto.UserResponse;
import org.example.hackaton01.user.infrastructure.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        validateRegistration(request);

        // Crear usuario con ModelMapper
        User user = modelMapper.map(request, User.class);
        user.setRole(UserRole.valueOf("ROLE_" + request.getRole()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (user.getRole() == UserRole.ROLE_BRANCH) {
            user.setBranch(request.getBranch());
        } else {
            user.setBranch(null);
        }

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                this,
                savedUser,
                request.getPassword()
        ));

        // Convertir a Response con ModelMapper
        UserResponse response = modelMapper.map(savedUser, UserResponse.class);
        response.setRole(savedUser.getRole().name().replace("ROLE_", ""));
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();


            String token = jwtUtil.generateToken(user, user.getRole().name(), user.getBranch());

            return AuthResponse.builder()
                    .token(token)
                    .expiresIn(jwtUtil.getExpirationTime())
                    .role(user.getRole().name().replace("ROLE_", ""))
                    .branch(user.getBranch())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }


    private void validateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya existe");
        }

        try {
            UserRole.valueOf("ROLE_" + request.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role debe ser CENTRAL o BRANCH");
        }

        if (request.getRole().equals("BRANCH") &&
                (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new RuntimeException("Branch es obligatorio para BRANCH");
        }
        if (request.getRole().equals("CENTRAL") && request.getBranch() != null) {
            throw new RuntimeException("CENTRAL no debe tener branch");
        }
    }
}