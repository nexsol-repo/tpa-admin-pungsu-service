package com.nexsol.tpa.client.aligo;

public interface SmsSender {

    void sendSms(String phoneNumber, String name, String link);

}