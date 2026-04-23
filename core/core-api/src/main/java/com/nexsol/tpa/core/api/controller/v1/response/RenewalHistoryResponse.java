package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.RenewalHistory;

import java.time.LocalDateTime;
import java.util.List;

public record RenewalHistoryResponse(List<RenewalHistoryItem> history) {

    public static RenewalHistoryResponse of(List<RenewalHistory> histories) {
        List<RenewalHistoryItem> items = histories.stream().map(RenewalHistoryItem::of).toList();

        return new RenewalHistoryResponse(items);
    }

    public record RenewalHistoryItem(Integer no, Integer contractId, Integer renewSeq, String joinStatus,
            LocalDateTime insuranceStartDate, LocalDateTime insuranceEndDate, Long applyCost, boolean isCurrent) {

        public static RenewalHistoryItem of(RenewalHistory history) {
            return new RenewalHistoryItem(history.renewSeq() + 1, history.contractId(), history.renewSeq(),
                    resolveJoinStatus(history.joinCheck()), history.insuranceStartDate(), history.insuranceEndDate(),
                    history.applyCost(), history.isCurrent());
        }

        private static String resolveJoinStatus(String joinCheck) {
            if (joinCheck == null) {
                return "알수없음";
            }
            return switch (joinCheck) {
                case "Y" -> "가입완료";
                case "N" -> "미가입";
                case "W" -> "대기";
                case "R" -> "접수";
                case "D" -> "해지";
                case "C" -> "임의해지";
                case "F" -> "가입오류";
                case "X" -> "보험만료";
                default -> joinCheck;
            };
        }

    }

}
