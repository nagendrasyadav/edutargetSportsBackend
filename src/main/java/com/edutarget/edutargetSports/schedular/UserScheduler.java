package com.edutarget.edutargetSports.schedular;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class UserScheduler {
    // Alternative with CRON (every 14 minutes)
     @Scheduled(cron = "0 */14 * * * *")
    public void runTaskWithCron() {
        log.info("Cron Scheduler triggered at {}", LocalDateTime.now());
    }
}
