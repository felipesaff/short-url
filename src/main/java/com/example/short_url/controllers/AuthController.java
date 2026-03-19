package com.example.short_url.controllers;

import com.example.short_url.domain.user.User;
import com.example.short_url.dto.AuthResponseDTO;
import com.example.short_url.dto.LoginRequestDTO;
import com.example.short_url.dto.RegisterRequestDTO;
import com.example.short_url.infra.security.TokenService;
import com.example.short_url.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepository
                .findByUsername(body.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(body.password(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.userRepository.findByUsername(body.username());
        if(user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User newUser = new User();
        newUser.setUsername(body.username());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setCreatedAt(Instant.now());
        newUser.setUpdatedAt(Instant.now());
        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
