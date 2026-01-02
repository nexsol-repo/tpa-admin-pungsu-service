package com.nexsol.tpa.support.mailer;

import com.nexsol.tpa.core.enums.MailType;

public interface EmailSender {

    void send(String toEmail, MailType mailType, String link, String name);

}
