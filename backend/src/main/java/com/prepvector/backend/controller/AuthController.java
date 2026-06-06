package com.prepVector.backend.controller;

import com.prepvector.backend.dto.AuthResponse;
import com.prepvector.backend.dto.LoginRequest;
import com.prepvector.backend.dto.SignupRequest;
import com.prepvector.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        String response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean isValid = userService.validateUser(
                request.getEmail(),
                request.getPassword()
        );
        if (isValid) {
            return ResponseEntity.ok("Login successful — JWT coming Day 3");
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}