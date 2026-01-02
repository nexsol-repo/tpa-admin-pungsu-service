package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.core.enums.ServiceType;

public record CreateSystemLogRequest(String content, ServiceType serviceType) {
}