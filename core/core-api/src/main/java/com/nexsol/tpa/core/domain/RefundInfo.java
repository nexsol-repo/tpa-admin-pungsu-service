package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RefundInfo(Long refundAmount, // 환불 금액
        String refundMethod, // 환불 방법
        LocalDateTime refundDt, // 환불 일시
        String refundReason // 환불 사유
) {

}