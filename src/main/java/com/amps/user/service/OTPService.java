package com.amps.user.service;

import com.amps.user.dto.OTPDetails;
import com.amps.user.model.User;
import com.amps.user.model.UserMfaDetails;
import jakarta.servlet.http.HttpServletRequest;

public interface OTPService {
    OTPDetails generateOTP(String userIdentifier);

    boolean validateOTP(UserMfaDetails mfaDetails);

    User userAuthentication(User user, HttpServletRequest httpRequest, String fingerPrintInfo, String uniqueId, boolean isForgotPassword);

    User OTPNotification(User user, boolean hasUntrustedRecord, String ipAddress, String systemInfo, UserMfaDetails mfaProvider, boolean isForgotPassword);

    boolean validateOTP(String otp, int userId, boolean trustThisComputer, boolean isForgotPasswordOtp);

    String generateDeviceById();
}
