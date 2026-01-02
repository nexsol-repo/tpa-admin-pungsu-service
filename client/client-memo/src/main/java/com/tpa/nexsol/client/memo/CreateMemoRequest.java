package com.tpa.nexsol.client.memo;

import com.nexsol.tpa.core.enums.ServiceType;

public record CreateMemoRequest(String content, ServiceType serviceType) {
}
