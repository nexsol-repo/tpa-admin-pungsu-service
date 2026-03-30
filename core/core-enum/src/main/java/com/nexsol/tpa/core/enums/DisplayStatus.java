package com.nexsol.tpa.core.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum DisplayStatus {

    DRAFT("임시저장"), APPLIED("신청완료"), JOINED("가입완료"), EXPIRING_SOON("만기임박"), EXPIRED("기간만료"), CANCELLED("임의해지"),
    FAILED("가입오류");

    private final String description;

    DisplayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 보험종료일 기준 만기임박 시작일 계산. - 종료일 16~말일: D-30 (종료 30일 전) - 종료일 1~15일: 전월 16일
     */
    public static LocalDate calculateExpiringSoonStart(LocalDate insuranceEndDate) {
        int dayOfMonth = insuranceEndDate.getDayOfMonth();
        if (dayOfMonth >= 16) {
            return insuranceEndDate.minusDays(30);
        }
        else {
            return insuranceEndDate.minusMonths(1).withDayOfMonth(16);
        }
    }

    public static boolean isExpiringSoon(LocalDateTime insuranceEndDate) {
        if (insuranceEndDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate endDate = insuranceEndDate.toLocalDate();
        if (!endDate.isAfter(today)) {
            return false;
        }
        LocalDate expiringSoonStart = calculateExpiringSoonStart(endDate);
        return !today.isBefore(expiringSoonStart);
    }

    public static DisplayStatus resolve(String joinCheck, String payYn, LocalDateTime insuranceEndDate) {
        if ("W".equals(joinCheck)) {
            return DRAFT;
        }
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
        if ("Y".equals(joinCheck) && isExpiringSoon(insuranceEndDate)) {
            return EXPIRING_SOON;
        }
        return JOINED;
    }

}