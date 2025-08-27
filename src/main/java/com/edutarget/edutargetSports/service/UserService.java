package com.edutarget.edutargetSports.service;

import com.edutarget.edutargetSports.dto.UserRegistrationRequest;
import com.edutarget.edutargetSports.dto.UserResponse;
import com.edutarget.edutargetSports.entity.UserRegistration;
import com.edutarget.edutargetSports.entity.UserStatus;
import com.edutarget.edutargetSports.exception.*;
import com.edutarget.edutargetSports.entity.AppUser;
import com.edutarget.edutargetSports.entity.Role;
import com.edutarget.edutargetSports.repository.UserRepository;
import com.edutarget.edutargetSports.util.StringUtilsHelper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRegistrationRequest request, AppUser creator) throws BadRequestException {

        // ADMIN cannot create POWER_ADMIN
        if (creator.getRole() == Role.ADMIN && "POWER_ADMIN".equalsIgnoreCase(request.getRole())) {
            throw new InvalidOperationException("ADMIN cannot create POWER_ADMIN");
        }

        if (Role.POWER_ADMIN.name().equalsIgnoreCase(request.getRole())) {
            throw new InvalidRoleAssignmentException("POWER_ADMIN cannot be created via APP. Please contact Admin.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required"); // custom exception
        }

        validateUser(request.getRole());

        String formattedName = StringUtilsHelper.capitalizeWords(request.getName());

        String newUniqueId = generateUniqueId();
        UserRegistration userRegistration = UserRegistration.builder()
                .uniqueId(newUniqueId)
                .name(formattedName)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .userStatus(UserStatus.valueOf(request.getUserStatus().toUpperCase()))
                .build();

        UserRegistration savedUserRegistration = userRepository.save(userRegistration);
        logger.info("User [{} - {}] created successfully by [{} - {}]",
                savedUserRegistration.getUniqueId(), savedUserRegistration.getName(),
                creator.getUniqueId(), creator.getName());
        return convertToUserResponse(savedUserRegistration);
    }

    private String generateUniqueId() {
        long nextId = userRepository.findTopByOrderByIdDesc()
                .map(userRegistration -> userRegistration.getId() + 1)
                .orElse(1L);
        return "ETU" + nextId;
    }

    public UserResponse updateUser(String uniqueId, UserRegistrationRequest request, AppUser creator) {
        // cross-check uniqueId in request (if client sends it)
        if (request.getUniqueId() != null && !request.getUniqueId().equalsIgnoreCase(uniqueId)) {
            throw new UniqueIdMismatchException("UniqueId in path [" + uniqueId +
                    "] does not match request body [" + request.getUniqueId() + "]");
        }
        UserRegistration userRegistration = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));

        if (creator.getRole() == Role.ADMIN) {
            if (userRegistration.getRole() == Role.POWER_ADMIN) {
                throw new InvalidOperationException("ADMIN cannot update POWER_ADMIN");
            }
        }

        validateUser(request.getRole());
        String formattedName = StringUtilsHelper.capitalizeWords(request.getName());

        userRegistration.setName(formattedName);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userRegistration.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRegistration.setRole(Role.valueOf(request.getRole().toUpperCase()));
        if (request.getUserStatus() != null) {
            userRegistration.setUserStatus(UserStatus.valueOf(request.getUserStatus().toUpperCase()));
        }

        UserRegistration userRegistrationEntity = userRepository.save(userRegistration);
        logger.info("User [{} - {}] updated successfully by [{} - {}]",
                userRegistrationEntity.getUniqueId(), userRegistrationEntity.getName(),
                creator.getUniqueId(), creator.getName());
        return convertToUserResponse(userRegistrationEntity);
    }

    private static void validateUser(String request) {
        Role role;
        try {
            role = Role.valueOf(request.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRoleException("Invalid role: " + request +
                    ". Allowed values are: " + Arrays.toString(Role.values()));
        }
    }

    // 🔹 Get one user
    public UserResponse getUser(String uniqueId) {
        UserRegistration userRegistration = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));
        return convertToUserResponse(userRegistration);
    }

    // 🔹 Get all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    // 🔹 Activate/Inactivate user
    public UserResponse changeUserStatus(String uniqueId, boolean active, AppUser creator) {
        UserRegistration userRegistration = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));

        // ADMIN cannot update POWER_ADMIN
        if (creator.getRole() == Role.ADMIN) {
            if (userRegistration.getRole() == Role.POWER_ADMIN) {
                throw new InvalidOperationException("ADMIN cannot change status of POWER_ADMIN");
            }
        }
        if ((active && userRegistration.getUserStatus() == UserStatus.ACTIVE) ||
                (!active && userRegistration.getUserStatus() == UserStatus.SUSPENDED)) {

            String msg = active
                    ? "User " + uniqueId + " is already active"
                    : "User " + uniqueId + " is already inactive";
            throw new InvalidOperationException(msg);
        }
        UserStatus status = active ? UserStatus.ACTIVE : UserStatus.SUSPENDED;
        userRegistration.setUserStatus(status);
        UserRegistration userRegistrationEntity = userRepository.save(userRegistration);
        return convertToUserResponse(userRegistrationEntity);
    }

    private UserResponse convertToUserResponse(UserRegistration userRegistration) {
        return UserResponse.builder()
                .uniqueId(userRegistration.getUniqueId())
                .name(userRegistration.getName())
                .role(userRegistration.getRole().name())
                .userStatus(userRegistration.getUserStatus().name())
                .userDisplay("[" + userRegistration.getUniqueId() + "] - " + userRegistration.getName())
                .build();
    }

}
