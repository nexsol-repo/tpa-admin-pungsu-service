package com.nexsol.tpa.core.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ContractKeyGenerator {

    public String generate() {
        // 날짜(14자리) + 랜덤문자(6자리) 조합
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + RandomStringUtils.randomAlphanumeric(6);
    }

}