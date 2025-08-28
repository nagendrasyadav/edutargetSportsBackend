package com.edutarget.edutargetSports.schedular;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserApiScheduler {

    private static final Logger log = LoggerFactory.getLogger(UserApiScheduler.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Runs every 14 minutes
    @Scheduled(cron = "0 */2 * * * *")
    public void hitApi() {
        String url = "http://localhost:8080/api/studentRegistrations/1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEVU1NWSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzU2Mzk5Nzg2LCJleHAiOjE3NTY0MDMzODZ9.R774mRA_1CH_PtaVpyT-whmXW6Y9rzKbiftTukxwlNo");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        log.info("Scheduler hit API. Response status: {}, body length={}",
                response.getStatusCode(), response.getBody() != null ? response.getBody().length() : 0);

    }
}
