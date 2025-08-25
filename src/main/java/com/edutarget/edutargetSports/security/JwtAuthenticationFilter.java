package com.edutarget.edutargetSports.security;

import com.edutarget.edutargetSports.exception.JwtTokenExpiredException;
import com.edutarget.edutargetSports.exception.JwtTokenInvalidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                String uniqueId = tokenProvider.getUniqueId(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(uniqueId);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                // make uniqueId available to controllers for logging/actions
                request.setAttribute("loggedInUser", uniqueId);
            }
        } catch (JwtTokenExpiredException ex) {
            logger.error("JWT expired: {}", ex.getMessage());
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Session expired. Please login again.");
            return;
        } catch (JwtTokenInvalidException ex) {
            logger.error("Invalid JWT: {}", ex.getMessage());
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token. Please login again.");
            return;
        } catch (Exception ex) {
            logger.error("Could not set authentication: {}", ex.getMessage());
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication failed. Please login again.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response,
                                  HttpStatus status,
                                  String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
