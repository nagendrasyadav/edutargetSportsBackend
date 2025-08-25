package com.edutarget.edutargetSports.service;

import com.edutarget.edutargetSports.dto.UserRegistrationRequest;
import com.edutarget.edutargetSports.dto.UserResponse;
import com.edutarget.edutargetSports.exception.*;
import com.edutarget.edutargetSports.entity.AppUser;
import com.edutarget.edutargetSports.entity.Role;
import com.edutarget.edutargetSports.entity.User;
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
        User user = User.builder()
                .uniqueId(newUniqueId)
                .name(formattedName)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .active(request.getActive())
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User [{} - {}] created successfully by [{} - {}]",
                savedUser.getUniqueId(), savedUser.getName(),
                creator.getUniqueId(), creator.getName());
        return convertToUserResponse(savedUser);
    }

    private String generateUniqueId() {
        long nextId = userRepository.findTopByOrderByIdDesc()
                .map(user -> user.getId() + 1)
                .orElse(1L);
        return "ETU" + nextId;
    }

    public UserResponse updateUser(String uniqueId, UserRegistrationRequest request, AppUser creator) {
        // cross-check uniqueId in request (if client sends it)
        if (request.getUniqueId() != null && !request.getUniqueId().equalsIgnoreCase(uniqueId)) {
            throw new UniqueIdMismatchException("UniqueId in path [" + uniqueId +
                    "] does not match request body [" + request.getUniqueId() + "]");
        }
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));

        if (creator.getRole() == Role.ADMIN) {
            if (user.getRole() == Role.POWER_ADMIN) {
                throw new InvalidOperationException("ADMIN cannot update POWER_ADMIN");
            }
        }

        validateUser(request.getRole());
        String formattedName = StringUtilsHelper.capitalizeWords(request.getName());

        user.setName(formattedName);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User userEntity = userRepository.save(user);
        logger.info("User [{} - {}] updated successfully by [{} - {}]",
                userEntity.getUniqueId(), userEntity.getName(),
                creator.getUniqueId(), creator.getName());
        return convertToUserResponse(userEntity);
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
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));
        return convertToUserResponse(user);
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
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + uniqueId));

        // ADMIN cannot update POWER_ADMIN
        if (creator.getRole() == Role.ADMIN) {
            if (user.getRole() == Role.POWER_ADMIN) {
                throw new InvalidOperationException("ADMIN cannot change status of POWER_ADMIN");
            }
        }
        if (user.isActive() == active) {
            String msg = active
                    ? "User " + uniqueId + " is already active"
                    : "User " + uniqueId + " is already inactive";
            throw new InvalidOperationException(msg);
        }

        user.setActive(active);
        User userEntity = userRepository.save(user);
        return convertToUserResponse(userEntity);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .uniqueId(user.getUniqueId())
                .name(user.getName())
                .role(user.getRole().name())
                .active(user.isActive())
                .userDisplay("[" + user.getUniqueId() + "] - " + user.getName())
                .build();
    }

}
