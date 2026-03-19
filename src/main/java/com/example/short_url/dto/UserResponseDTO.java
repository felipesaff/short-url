package com.example.short_url.dto;

import java.time.Instant;

public record UserResponseDTO(Long id, String username, Instant createdAt, Instant updatedAt) {
}
