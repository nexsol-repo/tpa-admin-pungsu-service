package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.enums.DateType;
import com.nexsol.tpa.core.enums.DisplayStatus;

import java.time.LocalDate;
import java.util.List;

public record BulkNotificationSendRequest(DateType dateType, LocalDate startDate, LocalDate endDate,
        List<DisplayStatus> statuses) {
}