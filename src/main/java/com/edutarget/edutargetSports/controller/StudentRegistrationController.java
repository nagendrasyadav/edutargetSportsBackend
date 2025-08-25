package com.edutarget.edutargetSports.controller;

import com.edutarget.edutargetSports.dto.*;
import com.edutarget.edutargetSports.exception.ResourceNotFoundException;
import com.edutarget.edutargetSports.exception.UserInactiveException;
import com.edutarget.edutargetSports.entity.AppUser;
import com.edutarget.edutargetSports.repository.AppUserRepository;
import com.edutarget.edutargetSports.service.StudentRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/studentRegistrations")
@RequiredArgsConstructor
public class StudentRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(StudentRegistrationController.class);
    private final StudentRegistrationService studentRegistrationService;
    private final AppUserRepository appUserRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','POWER_ADMIN')")
    public ResponseEntity<StudentRegistrationResponse> create(@Valid @RequestBody StudentRegistrationCreateRequest req, HttpServletRequest httpRequest) {
        log.debug("HTTP POST /api/registrations payload received");
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(!appUser.isActive()){
            throw new UserInactiveException("User is not active");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(studentRegistrationService.create(req, appUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','POWER_ADMIN')")
    public ResponseEntity<StudentRegistrationResponse> get(@PathVariable Long id,HttpServletRequest httpRequest) {
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(!appUser.isActive()){
            throw new UserInactiveException("User is not active");
        }
        log.debug("HTTP GET /api/registrations/{}", id);
        return ResponseEntity.ok(studentRegistrationService.getById(id,appUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','POWER_ADMIN')")
    public ResponseEntity<Page<StudentRegistrationResponse>> list(
            Pageable pageable,
            @RequestParam(required = false) String studentClassLabel,
            @RequestParam(required = false) Boolean feesPaid,
            @RequestParam(required = false) Integer jerseySize,
            @RequestParam(required = false, name = "name") String nameLike,
            HttpServletRequest httpRequest
    ) {
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(!appUser.isActive()){
            throw new UserInactiveException("User is not active");
        }
        log.debug("HTTP GET /api/registrations (list) called");
        var filter = new StudentRegistrationFilter(studentClassLabel, feesPaid, jerseySize, nameLike);
        return ResponseEntity.ok(studentRegistrationService.list(pageable, filter,appUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','POWER_ADMIN')")
    public ResponseEntity<StudentRegistrationResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody StudentRegistrationUpdateRequest request,
                                                           HttpServletRequest httpRequest) {
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(!appUser.isActive()){
            throw new UserInactiveException("User is not active");
        }
        log.debug("HTTP PUT /api/registrations/{} payload received", id);
        return ResponseEntity.ok(studentRegistrationService.update(id, request, appUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','POWER_ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id,HttpServletRequest httpRequest) {
        String loggedInUser = (String) httpRequest.getAttribute("loggedInUser");
        AppUser appUser = appUserRepository.findByUniqueId(loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        if(!appUser.isActive()){
            throw new UserInactiveException("User is not active");
        }
        log.debug("HTTP DELETE /api/registrations/{}", id);
        studentRegistrationService.delete(id,appUser);
        Map<String, Object> body = new HashMap<>();
        body.put("status", 200);
        body.put("message", "Student registration with ID:  " + id + " deleted successfully");

        return ResponseEntity.ok(body);
    }
}

