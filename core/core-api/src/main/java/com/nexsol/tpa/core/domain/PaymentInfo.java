package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentInfo(String payStatus, // 결제 상태 (N:결제전, Y:결제완료, C:환불완료)
        String payMethod, // 결제 방법 (CARD:신용카드, BANK:계좌이체, VBANK:가상계좌, DBANK:무통장입금)
        LocalDateTime payDt, // 결제 일시
        Long applyCost, // 결제 금액 (적용 보험료)
        RefundInfo refund // 환불 정보
) {

}