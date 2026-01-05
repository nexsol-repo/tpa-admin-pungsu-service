package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuredInfo(String name, String businessNumber, String birthDate, String email, String phoneNumber) {
}
