package com.edutarget.edutargetSports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentRegistrationResponse(
        Long id,
        String studentName,
        String studentClassLabel,
        Integer jerseySize,
        String nameOnJersey,
        String jerseyNumber,
        boolean feesPaid,
        BigDecimal feesAmountPaid,
        LocalDate feesPaidDate,
        String feesCollectedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

