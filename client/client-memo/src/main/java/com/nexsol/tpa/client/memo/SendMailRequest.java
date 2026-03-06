package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.core.enums.ServiceType;

import java.util.List;

public record SendMailRequest(ServiceType serviceType, List<String> emails, String title, String content) {
}