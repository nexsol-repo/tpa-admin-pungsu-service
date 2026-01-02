package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.core.enums.ServiceType;

public record CreateNotificationRequest(String type, String content, ServiceType serviceType) {
}