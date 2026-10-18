package com.myProject.controller;

import com.myProject.dto.response.AuthResponse;
import com.myProject.dto.request.SignInRequest;
import com.myProject.dto.request.SignupRequest;
import com.myProject.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;

    // Sign-in
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
    
    // Sign-up
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignupRequest request) {
        authService.signUp(request);
        return ResponseEntity.noContent().build();
    }
}
