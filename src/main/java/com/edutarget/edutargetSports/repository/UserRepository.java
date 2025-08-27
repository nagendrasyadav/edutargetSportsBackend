package com.edutarget.edutargetSports.repository;
import com.edutarget.edutargetSports.entity.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserRegistration, Long> {
    Optional<UserRegistration> findByUniqueId(String uniqueId);
    Optional<UserRegistration> findTopByOrderByIdDesc();
}
