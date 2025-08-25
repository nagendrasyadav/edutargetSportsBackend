package com.edutarget.edutargetSports.controller;
import com.edutarget.edutargetSports.dto.LoginRequest;
import com.edutarget.edutargetSports.entity.User;
import com.edutarget.edutargetSports.repository.UserRepository;
import com.edutarget.edutargetSports.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

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

    // DTO response inner class or separate file
    public record TokenResponse(String token, String tokenType, String role) {}
}
