package com.edutarget.edutargetSports.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_registrations")
@Getter
@Setter
public class StudentRegistration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student details
    @Column(nullable = false, length = 120)
    private String studentName;

    @Column(nullable = false, length = 32)
    private String studentClassLabel; // normalized "10-A"

    @Column(nullable = false)
    private Integer jerseySize; // e.g. 26..48

    @Column(nullable = false, length = 20)
    private String nameOnJersey;

    @Column(nullable = false, length = 4)
    private String jerseyNumber; // string to allow leading zeros

    // Fees (CRUD-controlled; validated by service)
    @Column(nullable = false)
    private boolean feesPaid;

    @Column(precision = 10, scale = 2)
    private BigDecimal feesAmountPaid;

    private LocalDate feesPaidDate;

    @Column(length = 120)
    private String feesCollectedBy; // who collected (any role) — taken from principal

    // Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 120, updatable = false)
    private String createdBy;
    @Column(length = 120)
    private String updatedBy;

    @PrePersist
    void onCreate() {
        var now = LocalDateTime.now();
        createdAt = now; updatedAt = now;
        if (!feesPaid) {
            feesAmountPaid = null;
            feesPaidDate = null;
            feesCollectedBy = null;
        }
    }

    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
