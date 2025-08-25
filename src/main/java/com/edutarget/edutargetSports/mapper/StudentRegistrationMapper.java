package com.edutarget.edutargetSports.mapper;

import com.edutarget.edutargetSports.dto.*;
import com.edutarget.edutargetSports.entity.StudentRegistration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;


public final class StudentRegistrationMapper {
    private StudentRegistrationMapper() {}

    public static StudentRegistration toEntity(StudentRegistrationCreateRequest req) {
        var e = new StudentRegistration();
        e.setStudentName(normalizeName(req.studentName().trim()));
        e.setStudentClassLabel(normalizeClass(req.studentClassLabel()));
        e.setJerseySize(req.jerseySize());
        e.setNameOnJersey(req.nameOnJersey().trim().toUpperCase());
        e.setJerseyNumber(req.jerseyNumber());
        e.setFeesPaid(Boolean.TRUE.equals(req.feesPaid()));
        e.setFeesAmountPaid(req.feesAmountPaid());
        e.setFeesPaidDate(parseDate(req.feesPaidDate()));
        return e;
    }

    public static void updateEntity(StudentRegistrationUpdateRequest req, StudentRegistration e) {
        e.setStudentClassLabel(normalizeClass(req.studentClassLabel()));
        e.setJerseySize(req.jerseySize());
        e.setNameOnJersey(req.nameOnJersey().trim().toUpperCase());
        e.setJerseyNumber(req.jerseyNumber());
        e.setFeesPaid(Boolean.TRUE.equals(req.feesPaid()));
        e.setFeesAmountPaid(req.feesAmountPaid());
        e.setFeesPaidDate(parseDate(req.feesPaidDate()));
    }

    public static StudentRegistrationResponse toResponse(StudentRegistration e) {
        return new StudentRegistrationResponse(
                e.getId(),
                e.getStudentName(),
                e.getStudentClassLabel(),
                e.getJerseySize(),
                e.getNameOnJersey(),
                e.getJerseyNumber(),
                e.isFeesPaid(),
                e.getFeesAmountPaid(),
                e.getFeesPaidDate(),
                e.getFeesCollectedBy(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private static String normalizeClass(String label) {
        return label.trim();
    }

    private static LocalDate parseDate(String v) {
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
    }
    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) return null;
        return Arrays.stream(name.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
