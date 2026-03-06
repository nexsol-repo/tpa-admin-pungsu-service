package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.core.enums.ServiceType;

import java.util.List;

public record SendSmsRequest(ServiceType serviceType, List<String> receivers, String title, String content) {
}