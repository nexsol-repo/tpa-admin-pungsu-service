package com.nexsol.tpa.core.enums;

import java.time.LocalDateTime;

public enum DisplayStatus {

    APPLIED("신청완료"), JOINED("가입완료"), EXPIRING_SOON("만기임박"), EXPIRED("기간만료"), CANCELLED("임의해지"),
    FAILED("가입오류");

    private final String description;

    DisplayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static DisplayStatus resolve(String joinCheck, String payYn, LocalDateTime insuranceEndDate) {
        if ("F".equals(joinCheck) && "N".equals(payYn)) {
            return FAILED;
        }
        if ("C".equals(joinCheck)) {
            return CANCELLED;
        }
        if ("X".equals(joinCheck)) {
            return EXPIRED;
        }
        if ("N".equals(joinCheck) && "N".equals(payYn)) {
            return APPLIED;
        }
        if ("Y".equals(joinCheck) && insuranceEndDate != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysLater = now.plusDays(7);
            if (insuranceEndDate.isAfter(now) && insuranceEndDate.isBefore(sevenDaysLater)) {
                return EXPIRING_SOON;
            }
        }
        return JOINED;
    }

}