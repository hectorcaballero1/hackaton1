package org.example.hackaton01.auth.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hackaton01.auth.domain.AuthService;
import org.example.hackaton01.auth.dto.AuthResponse;
import org.example.hackaton01.auth.dto.LoginRequest;
import org.example.hackaton01.auth.dto.RegisterRequest;
import org.example.hackaton01.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
