package com.nexsol.tpa.core.api.scheduler;

import com.nexsol.tpa.core.domain.InsuredService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RenewalScheduler {

    private final InsuredService insuredService;

//    @Scheduled(cron = "0 0 10 * * *")
//    public void run() {
//        insuredService.sendRenewalNotifications();
//    }

    @Scheduled(cron = "0 0 10 * * *")
    public void run() {
        LocalDate sevenDaysLater = LocalDate.now().plusDays(7);
        insuredService.sendRenewalNotificationsByEndDate(sevenDaysLater);
    }

}