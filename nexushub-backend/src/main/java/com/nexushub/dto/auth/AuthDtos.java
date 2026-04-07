package com.nexushub.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    // ── Register Request ──────────────────────────────────────────────

    public record RegisterRequest(
            @NotBlank(message = "Name is required")
            @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
            String name,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
            String password
    ) {}

    // ── Login Request ─────────────────────────────────────────────────

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    // ── Auth Response ─────────────────────────────────────────────────

    public record AuthResponse(
            Long userId,
            String name,
            String email,
            String token
    ) {}
}
