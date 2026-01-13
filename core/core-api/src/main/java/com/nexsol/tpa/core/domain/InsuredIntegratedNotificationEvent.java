package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.MailType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsuredIntegratedNotificationEvent(

        Integer contractId, String name, String email, String phoneNumber, MailType type, String link, String writerId,
        String token, LocalDateTime applicationDate, String account, String payYn, String policyNumber) {
}
