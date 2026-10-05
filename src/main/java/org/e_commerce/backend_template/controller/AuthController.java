package org.e_commerce.backend_template.controller;

import org.e_commerce.backend_template.dto.AuthResponseDto;
import org.e_commerce.backend_template.dto.LoginRequestDto;
import org.e_commerce.backend_template.dto.RegisterUserRequestDto;
import org.e_commerce.backend_template.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid final RegisterUserRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid final LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }
}
