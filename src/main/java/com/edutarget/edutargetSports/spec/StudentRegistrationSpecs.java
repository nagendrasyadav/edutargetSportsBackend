package com.edutarget.edutargetSports.spec;

import com.edutarget.edutargetSports.dto.StudentRegistrationFilter;
import com.edutarget.edutargetSports.entity.StudentRegistration;
import org.springframework.data.jpa.domain.Specification;

public final class StudentRegistrationSpecs {
    private StudentRegistrationSpecs() {}

    public static Specification<StudentRegistration> withFilter(StudentRegistrationFilter f) {
        return Specification.where(byClass(f.studentClassLabel()))
                .and(byFeesPaid(f.feesPaid()))
                .and(byJerseySize(f.jerseySize()))
                .and(byNameLike(f.nameLike()));
    }

    private static Specification<StudentRegistration> byClass(String classLabel) {
        if (classLabel == null || classLabel.isBlank()) return null;
        String normalized = classLabel.trim().toUpperCase().replace(' ', '-');
        return (root, q, cb) -> cb.equal(cb.upper(root.get("studentClassLabel")), normalized);
    }

    private static Specification<StudentRegistration> byFeesPaid(Boolean feesPaid) {
        if (feesPaid == null) return null;
        return (root, q, cb) -> cb.equal(root.get("feesPaid"), feesPaid);
    }

    private static Specification<StudentRegistration> byJerseySize(Integer jerseySize) {
        if (jerseySize == null) return null;
        return (root, q, cb) -> cb.equal(root.get("jerseySize"), jerseySize);
    }

    private static Specification<StudentRegistration> byNameLike(String nameLike) {
        if (nameLike == null || nameLike.isBlank()) return null;
        String pattern = "%" + nameLike.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("studentName")), pattern);
    }
}
