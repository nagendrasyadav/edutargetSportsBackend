package com.edutarget.edutargetSports.controller;

import com.edutarget.edutargetSports.dto.UserRegistrationRequest;
import com.edutarget.edutargetSports.dto.UserResponse;
import com.edutarget.edutargetSports.entity.UserStatus;
import com.edutarget.edutargetSports.exception.ResourceNotFoundException;
import com.edutarget.edutargetSports.exception.UserInactiveException;
import com.edutarget.edutargetSports.entity.AppUser;
import com.edutarget.edutargetSports.repository.AppUserRepository;
import com.edutarget.edutargetSports.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest req,
                                                     HttpServletRequest httpRequest) throws BadRequestException {
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(appUser.getUserStatus().equals(UserStatus.SUSPENDED)){
            throw new UserInactiveException("User is not active");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(req, appUser));
    }

    // 🔹 Update user (POWER_ADMIN only)
    @PutMapping("/{uniqueId}")
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String uniqueId,
                                                   @Valid @RequestBody UserRegistrationRequest req,
                                                   HttpServletRequest request) throws Exception {
        String loggedInUserId = (String) request.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(appUser.getUserStatus().equals(UserStatus.SUSPENDED)){
            throw new UserInactiveException("User is not active");
        }

        return ResponseEntity.ok(userService.updateUser(uniqueId, req, appUser));
    }

    // 🔹 Get one user
    @GetMapping("/{uniqueId}")
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable String uniqueId, HttpServletRequest request) throws Exception {
        String loggedInUserId = (String) request.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found..!"));
        if(appUser.getUserStatus().equals(UserStatus.SUSPENDED)){
            throw new UserInactiveException("User is not active");
        }
        return ResponseEntity.ok(userService.getUser(uniqueId));
    }

    // 🔹 Get all users
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(HttpServletRequest request) throws Exception {
        String loggedInUserId = (String) request.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(appUser.getUserStatus().equals(UserStatus.SUSPENDED)){
            throw new UserInactiveException("User is not active");
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔹 Activate/Inactivate user
    @PatchMapping("/{uniqueId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<UserResponse> changeStatus(@PathVariable String uniqueId,
                                                     @RequestParam boolean active, HttpServletRequest request) throws Exception {
        String loggedInUserId = (String) request.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(appUser.getUserStatus().equals(UserStatus.SUSPENDED)){
            throw new UserInactiveException("User is not active");
        }
        return ResponseEntity.ok(userService.changeUserStatus(uniqueId, active, appUser));
    }
}

