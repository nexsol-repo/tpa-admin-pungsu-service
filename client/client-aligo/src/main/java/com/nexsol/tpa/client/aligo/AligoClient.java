package com.nexsol.tpa.client.aligo;

import com.nexsol.tpa.client.aligo.config.AligoFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "aligo-client", url = "https://apis.aligo.in", configuration = AligoFeignConfig.class)
public interface AligoClient {

    @PostMapping(value = "/send/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            headers = "Accept=application/json")
    Map<String, Object> sendSms(@RequestBody MultiValueMap<String, String> params);

}