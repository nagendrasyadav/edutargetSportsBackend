package com.edutarget.edutargetSports.dto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StudentRegistrationCreateRequest(
        @Pattern(
                regexp = "^[A-Za-z ]*$",
                message = "Name must contain only alphabets and spaces"
        )
        @NotBlank(message = "Name is required")
        String studentName,

        @NotBlank(message = "Class cannot be blank")
        @Pattern(regexp = "^[0-9]{1,2}$", message = "Class must be a number, e.g., 8 or 10")
        String studentClassLabel,

        @NotNull(message = "Jersey size is required")
        @Min(value = 22, message = "Jersey size must be at least 22")
        @Max(value = 48, message = "Jersey size must not exceed 48")
        Integer jerseySize,

        @NotBlank(message = "nameOnJersey cannot be blank")
        @Size(min = 1, max = 30)
        String nameOnJersey,

        @NotBlank(message = "jerseyNumber cannot be blank")
        @Pattern(regexp = "^[0-9]{1,4}$", message = "1-4 digit jersey number")
        String jerseyNumber,

        // Fees controlled via CRUD here (no workflow)
        @NotNull(message = "feesPaid cannot be blank")
        Boolean feesPaid,

        @Digits(integer = 8, fraction = 2)
        @Positive(message = "Must be > 0")
        BigDecimal feesAmountPaid,

        // yyyy-MM-dd, validated at service level for consistency
        String feesPaidDate
) {}
