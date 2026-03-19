package com.example.short_url.controllers;

import com.example.short_url.domain.link.Link;
import com.example.short_url.domain.user.User;

import com.example.short_url.services.Base62Service;
import com.example.short_url.services.LinkService;
import com.example.short_url.utils.AppUrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;
    private final Base62Service base62Service;
    private final AppUrlUtil appUrlUtil;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        Link newLink = linkService.createLink(user, body.get("url"));
        String shortCode = base62Service.encode(newLink.getId());
        String shortUrl = appUrlUtil.getBaseUrl(request) + "/" + shortCode;

        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public void redirect(
            @PathVariable String shortCode,
            HttpServletResponse response
    ) throws IOException {
        try {
            String originalUrl = linkService.getOriginalUrlToRedirect(shortCode);
            response.sendRedirect(originalUrl);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Link not found");
        }
    }
}
