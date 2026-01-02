package com.nexsol.tpa.client.aligo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "aligo-client", url = "https://apis.aligo.in")
public interface AligoClient {

    @PostMapping(value = "/send/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> sendSms(@RequestParam Map<String, ?> params);

}
