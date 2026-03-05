package com.nexsol.tpa.core.enums;

public enum MailType {

    REJOIN("재가입 안내", "재가입창", "재가입 링크"),
    CERTIFICATE("가입확인서 안내", "가입확인서(PDF)창", "가입확인서 보기"),
    JOINED("가입 완료 안내", "가입완료", "증권/가입확인서 확인"),
    CANCELLED("임의 해지 안내", "임의해지", "가입 정보 확인하기");

    private final String titleSuffix;

    private final String targetName;

    private final String linkText;

    MailType(String titleSuffix, String targetName, String linkText) {
        this.titleSuffix = titleSuffix;
        this.targetName = targetName;
        this.linkText = linkText;
    }

    public String getTitle() {
        return "[TPA KOREA] " + titleSuffix;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTitleSuffix() {
        return titleSuffix;
    }

    public String getLinkText() {
        return linkText;
    }

}