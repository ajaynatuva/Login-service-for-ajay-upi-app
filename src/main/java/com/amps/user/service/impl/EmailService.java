package com.amps.user.service.impl;

import com.amps.user.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    public static final String EMAIL_SERVICE_URL = "/email/sendEmailForOTP";
    public static final String OTP_TEMPLATE = "OTPTemplate";
    @Value("${ipu.email.service.host.url}")
    String emailServiceEndpoint;
    @Autowired
    RestUtil restUtil;

    @Value("${env.name}")
    String environment;

    public void sendOtp(String toEmail, String otp, String userName, String message) {
        Map<String, String> emailParamsMap = new HashMap<String, String>();
        emailParamsMap.put("templateName", "OTPTemplate");
        emailParamsMap.put("otp", otp);
        emailParamsMap.put("userName", userName);
        emailParamsMap.put("message", message);
        emailParamsMap.put("recipients", toEmail);
        emailParamsMap.put("environment", environment.toUpperCase());
        restUtil.postMethod(emailServiceEndpoint + EMAIL_SERVICE_URL, emailParamsMap);
    }
}
