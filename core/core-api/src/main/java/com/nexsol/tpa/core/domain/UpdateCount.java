package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record UpdateCount(int totalCount, int successCount, int failCount) {
}
