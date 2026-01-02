package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.enums.MailType;

public record NotificationSendRequest(MailType type) {
}
