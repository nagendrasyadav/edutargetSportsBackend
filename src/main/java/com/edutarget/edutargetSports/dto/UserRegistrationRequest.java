package com.edutarget.edutargetSports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserRegistrationRequest {

    private String uniqueId;

    @Pattern(
            regexp = "^[A-Za-z ]*$",
            message = "Name must contain only alphabets and spaces"
    )
    @NotBlank(message = "Name is required")
    private String name;

    //@NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters long and contain one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // USER or ADMIN (POWER_ADMIN cannot be created via API)

    @NotBlank(message = "User Status is required")
    private String userStatus;

}
