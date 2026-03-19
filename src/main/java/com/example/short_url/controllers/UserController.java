package com.example.short_url.controllers;

import com.example.short_url.domain.user.User;
import com.example.short_url.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )
        );
    }
}
