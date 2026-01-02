package com.nexsol.tpa.core.enums;

public enum MailType {

    REJOIN("재가입 안내", "재가입창", "재가입 링크"), CERTIFICATE("가입확인서 안내", "가입확인서(PDF)창", "가입확인서 보기");

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

    public String getLinkText() {
        return linkText;
    }

}