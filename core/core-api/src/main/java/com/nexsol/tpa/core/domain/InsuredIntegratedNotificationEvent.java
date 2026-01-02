package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.MailType;

public record InsuredIntegratedNotificationEvent(

        Integer contractId, String name, String email, String phoneNumber, MailType type, // REJOIN,
        // CERTIFICATE
        // 등
        // (Enum
        // 활용)
        String link, String writerId, String token) {
}
