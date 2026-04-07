package com.nexushub.service;

import com.nexushub.dto.auth.AuthDtos.AuthResponse;
import com.nexushub.dto.auth.AuthDtos.LoginRequest;
import com.nexushub.dto.auth.AuthDtos.RegisterRequest;
import com.nexushub.entity.User;
import com.nexushub.exception.BadRequestException;
import com.nexushub.exception.UnauthorizedException;
import com.nexushub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered: " + request.email());
        }

        // NOTE: In production, use BCryptPasswordEncoder to hash the password.
        // Kept plain here for simplicity in this portfolio project.
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .authToken(generateToken())
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {} (id={})", saved.getEmail(), saved.getId());

        return toAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Rotate token on each login
        user.setAuthToken(generateToken());
        User saved = userRepository.save(user);
        log.info("User logged in: {} (id={})", saved.getEmail(), saved.getId());

        return toAuthResponse(saved);
    }

    public User validateToken(String token) {
        return userRepository.findByAuthToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token"));
    }

    // ── Private helpers ───────────────────────────────────────────────

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getAuthToken());
    }
}
