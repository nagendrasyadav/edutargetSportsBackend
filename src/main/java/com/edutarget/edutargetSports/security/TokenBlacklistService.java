package com.edutarget.edutargetSports.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // token -> expiry
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiresAt) {
        if (token != null && expiresAt != null) {
            blacklist.put(token, expiresAt);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) return false;

        Instant exp = blacklist.get(token);
        if (exp == null) return false;

        // if expired, remove and allow normal expiry
        if (Instant.now().isAfter(exp)) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
