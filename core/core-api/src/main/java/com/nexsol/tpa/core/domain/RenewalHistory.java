package com.nexsol.tpa.core.domain;

import java.time.LocalDateTime;

public record RenewalHistory(Integer contractId, String referIdx, Integer renewSeq, String joinCheck,
        LocalDateTime insuranceStartDate, LocalDateTime insuranceEndDate, Long applyCost, boolean isCurrent) {
}
