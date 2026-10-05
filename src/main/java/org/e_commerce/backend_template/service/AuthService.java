package org.e_commerce.backend_template.service;

import org.e_commerce.backend_template.config.JwtService;
import org.e_commerce.backend_template.dto.AuthResponseDto;
import org.e_commerce.backend_template.dto.LoginRequestDto;
import org.e_commerce.backend_template.dto.RegisterUserRequestDto;
import org.e_commerce.backend_template.entity.Role;
import org.e_commerce.backend_template.entity.User;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDto register(final RegisterUserRequestDto requestDto) {
        if (userRepository.existsByUsernameIgnoreCase(requestDto.username())) {
            throw new AppException("Ya existe un usuario con el username: " + requestDto.username(), HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new AppException("Ya existe un usuario con el email: " + requestDto.email(), HttpStatus.CONFLICT);
        }

        final Role role = requestDto.role() != null ? requestDto.role() : Role.ROLE_ADMIN;

        final User user = User.builder()
                .username(requestDto.username().trim())
                .email(requestDto.email().trim().toLowerCase())
                .password(passwordEncoder.encode(requestDto.password()))
                .fullName(requestDto.fullName())
                .role(role)
                .enabled(true)
                .build();

        final User saved = userRepository.save(user);
        final String jwt = jwtService.generateToken(saved);

        log.info("Usuario registrado: id={}, username={}, role={}", saved.getId(), saved.getUsername(), saved.getRole());
        return new AuthResponseDto(
                jwt,
                "Bearer",
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getRole()
        );
    }

    public AuthResponseDto login(final LoginRequestDto requestDto) {
        // Permitir login tanto con username como con email
        final User user = userRepository.findByUsernameIgnoreCase(requestDto.username())
                .or(() -> userRepository.findByEmailIgnoreCase(requestDto.username()))
                .orElseThrow(() -> new AppException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), requestDto.password())
        );

        final String jwt = jwtService.generateToken(user);
        log.info("Usuario autenticado exitosamente: id={}, username={}", user.getId(), user.getUsername());

        return new AuthResponseDto(
                jwt,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }
}
