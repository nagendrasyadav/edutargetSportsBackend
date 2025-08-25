package com.edutarget.edutargetSports.service;

import com.edutarget.edutargetSports.dto.*;
import com.edutarget.edutargetSports.mapper.StudentRegistrationMapper;
import com.edutarget.edutargetSports.entity.AppUser;
import com.edutarget.edutargetSports.entity.StudentRegistration;
import com.edutarget.edutargetSports.repository.StudentRegistrationRepository;
import com.edutarget.edutargetSports.exception.BadRequestException;
import com.edutarget.edutargetSports.exception.NotFoundException;
import com.edutarget.edutargetSports.spec.StudentRegistrationSpecs;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(StudentRegistrationService.class);
    private final StudentRegistrationRepository repo;

    public StudentRegistrationResponse create(StudentRegistrationCreateRequest request, AppUser appUser) {
        final String appUserName = appUser.getName();
        log.info("CREATE request by={} jerseyNumber={}", appUserName, request.jerseyNumber());

        if (repo.existsDuplicate(
                trimOrEmpty(request.studentName()),
                trimOrEmpty(request.studentClassLabel()),
                request.jerseySize(),
                request.jerseyNumber(),
                null)) {
            throw new BadRequestException("Duplicate registration for the same student, class, size and jersey number.");
        }
        validateFeesConsistency(request.feesPaid(), request.feesAmountPaid(), request.feesPaidDate());

        var e = StudentRegistrationMapper.toEntity(request);
        e.setFeesCollectedBy(e.isFeesPaid() ? appUserName : null); // who collected
        e.setCreatedBy(appUserName);
        e.setUpdatedBy(appUserName);

        e = repo.save(e);
        log.info("CREATE success by={} id={}", appUserName, e.getId());
        return StudentRegistrationMapper.toResponse(e);
    }


    @Transactional(readOnly = true)
    public StudentRegistrationResponse getById(Long id,AppUser appUser) {
        final String appUserName = appUser.getName();
        log.info("GET byId by={} id={}", appUserName, id);

        var e = getEntity(id);
        return StudentRegistrationMapper.toResponse(e);
    }


    @Transactional(readOnly = true)
    public Page<StudentRegistrationResponse> list(Pageable pageable, StudentRegistrationFilter filter,AppUser appUser) {
        final String appUserName = appUser.getName();
        log.info("LIST by={} filters[class={}, feesPaid={}, size={}, nameLike={}] page={}",
                appUserName, filter.studentClassLabel(), filter.feesPaid(), filter.jerseySize(), filter.nameLike(), pageable);

        var spec = StudentRegistrationSpecs.withFilter(filter);
        return repo.findAll(spec, pageable).map(StudentRegistrationMapper::toResponse);
    }


    public StudentRegistrationResponse update(Long id, StudentRegistrationUpdateRequest request,AppUser appUser) {
        final String appUserName = appUser.getName();
        log.info("UPDATE request by={} id={} jerseyNumber={}", appUserName, id, request.jerseyNumber());

        var e = getEntity(id);

        if (repo.existsDuplicate(
                trimOrEmpty(request.studentName()),
                trimOrEmpty(request.studentClassLabel()),
                request.jerseySize(),
                request.jerseyNumber(),
                id)) {
            throw new BadRequestException("Duplicate registration for the same student, class, size and jersey number.");
        }

        validateFeesConsistency(request.feesPaid(), request.feesAmountPaid(), request.feesPaidDate());

        // apply changes
        StudentRegistrationMapper.updateEntity(request, e);
        e.setFeesCollectedBy(e.isFeesPaid() ? appUserName : null);
        e.setUpdatedBy(appUserName);

        log.info("UPDATE success by={} id={}", appUserName, e.getId());
        return StudentRegistrationMapper.toResponse(e);
    }


    public void delete(Long id,AppUser appUser) {
        final String appUserName = appUser.getName();
        log.info("DELETE request by={} id={}", appUserName, id);

        if (!repo.existsById(id)) throw new NotFoundException("Student with ID : "+id+ " not found");
        repo.deleteById(id);
        log.info("DELETE success by={} id={}", appUserName, id);
    }

    // --- helpers ---
    @Transactional(readOnly = true)
    public StudentRegistration getEntity(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new NotFoundException("Student with ID : "+ id+" not found " ));
    }

    private void validateFeesConsistency(Boolean feesPaid, BigDecimal amount, String dateStr) {
        LocalDate date = (dateStr == null || dateStr.isBlank()) ? null : LocalDate.parse(dateStr);

        if (Boolean.TRUE.equals(feesPaid)) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("feesAmountPaid must be provided and > 0 when feesPaid is true.");
            }
            if (date == null) {
                throw new BadRequestException("feesPaidDate must be provided when feesPaid is true.");
            }
            if (date.isAfter(LocalDate.now())) {
                throw new BadRequestException("feesPaidDate cannot be in the future.");
            }
        } else {
            if (amount != null || date != null) {
                throw new BadRequestException("feesAmountPaid and feesPaidDate must be null when feesPaid is false.");
            }
        }
    }

    private static String trimOrEmpty(String v) { return v == null ? "" : v.trim(); }
}
