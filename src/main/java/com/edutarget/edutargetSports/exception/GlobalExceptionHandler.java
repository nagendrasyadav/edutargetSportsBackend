package com.edutarget.edutargetSports.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String error, String message, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(status.value(), error, message, path);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserExists(UserAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        logger.error("Validation failed: {}", sb.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", sb.toString(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials", "Username or password is incorrect", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied", "You are not authorized to perform this action", request);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiError> handleInvalidRole(InvalidRoleException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Role", ex.getMessage(), request);
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<ApiError> handleJwtExpired(JwtTokenExpiredException ex, WebRequest request) {
        logger.error("JWT Expired: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "JWT Expired", "Session expired. Please login again.", request);
    }

    @ExceptionHandler(JwtTokenInvalidException.class)
    public ResponseEntity<ApiError> handleJwtInvalid(JwtTokenInvalidException ex, WebRequest request) {
        logger.error("Invalid JWT: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT", "Invalid or malformed token", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiError> handleInvalidOperation(InvalidOperationException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Operation", ex.getMessage(), request);
    }

    @ExceptionHandler(UniqueIdMismatchException.class)
    public ResponseEntity<ApiError> handleUniqueIdMismatch(UniqueIdMismatchException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "UniqueId Mismatch", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
        logger.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something went wrong!", request);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ApiError> handleUserInactive(UserInactiveException ex, WebRequest request) {
        logger.warn("User inactive: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "User Inactive", ex.getMessage(), request);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, WebRequest request) {
        logger.warn("NOT_FOUND msg={}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, WebRequest request) {
        logger.warn("BAD_REQUEST msg={}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }
}

