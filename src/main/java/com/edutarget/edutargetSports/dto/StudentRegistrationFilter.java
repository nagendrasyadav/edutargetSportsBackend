package com.edutarget.edutargetSports.dto;

public record StudentRegistrationFilter(
        String studentClassLabel,
        Boolean feesPaid,
        Integer jerseySize,
        String nameLike
) {}

