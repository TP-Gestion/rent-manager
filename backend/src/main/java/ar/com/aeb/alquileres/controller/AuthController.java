package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.login.LoginRequest;
import ar.com.aeb.alquileres.dto.login.LoginResponse;
import ar.com.aeb.alquileres.dto.login.RegisterRequest;
import ar.com.aeb.alquileres.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Operations for user authentication")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse loginResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }
}
