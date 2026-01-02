package com.nexsol.tpa.core.domain;

public record ChangeDetail(String fieldName, String oldValue, String newValue) {
    @Override
    public String toString() {
        return String.format("[%s] %s -> %s", fieldName, (oldValue == null || oldValue.isBlank()) ? "없음" : oldValue,
                (newValue == null || newValue.isBlank()) ? "없음" : newValue);
    }
}