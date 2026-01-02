package com.nexsol.tpa.core.domain;

public record InsuredSystemLogEvent(Integer contractId, String content, String writerId, String token) {
}
