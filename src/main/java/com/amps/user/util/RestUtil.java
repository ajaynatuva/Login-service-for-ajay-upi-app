package com.amps.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RestUtil {

    Logger logger = LogManager.getLogger(RestUtil.class);

    public void postMethod(String url, Map<String, String> paramMap) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            String reqBodyData = new ObjectMapper().writeValueAsString(paramMap);
            HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, header);
            ResponseEntity<Object> result = restTemplate.postForEntity(url, requestEnty, Object.class);
            logger.info(result);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
