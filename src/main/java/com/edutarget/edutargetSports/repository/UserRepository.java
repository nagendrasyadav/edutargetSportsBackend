package com.edutarget.edutargetSports.repository;
import com.edutarget.edutargetSports.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUniqueId(String uniqueId);
    Optional<User> findTopByOrderByIdDesc();
}
