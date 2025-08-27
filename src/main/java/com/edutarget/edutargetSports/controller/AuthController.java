package com.edutarget.edutargetSports.controller;
import com.edutarget.edutargetSports.dto.LoginRequest;
import com.edutarget.edutargetSports.entity.User;
import com.edutarget.edutargetSports.repository.UserRepository;
import com.edutarget.edutargetSports.security.JwtTokenProvider;
import com.edutarget.edutargetSports.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUniqueId(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // fetch role from DB to embed in token
        User user = userRepository.findByUniqueId(userDetails.getUsername())
                .orElseThrow();

        String token = tokenProvider.createToken(user.getUniqueId(), user.getRole().name());

        logger.info("User [{}] logged in", user.getUniqueId());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", user.getRole().name()));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Authorization Bearer token is required",
                            "path", request.getRequestURI()
                    ));
        }

        // Put token into blacklist until it naturally expires
        Instant expiresAt = tokenProvider.getExpiry(token);
        tokenBlacklistService.blacklist(token, expiresAt);

        String uid = tokenProvider.getUniqueId(token);
        logger.info("User [{}] logged out. Token blacklisted until {}", uid, expiresAt);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Logged out successfully",
                "logoutAt", Instant.now().toString(),
                "expiresAt", expiresAt.toString(),
                "uniqueId", uid
        ));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    // DTO response inner class or separate file
    public record TokenResponse(String token, String tokenType, String role) {}


}
