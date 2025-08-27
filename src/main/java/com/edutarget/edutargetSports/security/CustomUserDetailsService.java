package com.edutarget.edutargetSports.security;

import com.edutarget.edutargetSports.entity.UserRegistration;
import com.edutarget.edutargetSports.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String uniqueId) throws UsernameNotFoundException {
        UserRegistration userRegistration = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + uniqueId));

        return org.springframework.security.core.userdetails.User
                .withUsername(userRegistration.getUniqueId())
                .password(userRegistration.getPassword())
                .authorities("ROLE_" + userRegistration.getRole().name())
                .build();
    }
}
