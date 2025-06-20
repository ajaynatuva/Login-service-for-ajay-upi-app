package com.amps.user.service.impl;

import com.amps.user.dao.MFAProviderDao;
import com.amps.user.dto.OTPDetails;
import com.amps.user.model.User;
import com.amps.user.model.UserMfaDetails;
import com.amps.user.repository.MFARepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.service.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OTPServiceImpl implements OTPService {

    private static final Random random = new Random();
    static String message = "log in to your account";

    private final Logger logger = LogManager.getLogger(OTPServiceImpl.class);

    @Autowired
    public MFAProviderDao mfaProviderDao;

    @Autowired
    UserLoginRepository userloginRepository;

    @Autowired
    MFARepository mfaRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder encryptCode;

    @Value("${trust.validity.days}")
    private int trustValidityDays;

    @Value("${otp.validity}")
    private long otpValidity;
//    private static final long OTP_VALID_DURATION = 1; // Duration in minutes

    public OTPDetails generateOTP(String userIdentifier) {
        String otp = String.valueOf(100000 + random.nextInt(900000));
        return new OTPDetails(otp, System.currentTimeMillis());
    }

    public boolean validateOTP(UserMfaDetails mfaDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = mfaDetails.getTsTokenExpiration();
        return Duration.between(expirationTime, now).toMinutes() <= otpValidity;
    }

    @Override
    public String generateDeviceById() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
                return fromBytes(mac) + ip;
            } else {
                return findMACAddressFromAllInterfaces() + ip;
            }
        } catch (UnknownHostException | SocketException e) {
            logger.error(e);
        }
        return null;
    }

    private String findMACAddressFromAllInterfaces() throws SocketException {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        while (networks.hasMoreElements()) {
            NetworkInterface network = networks.nextElement();
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
                var address = fromBytes(mac);
                logger.info(address);
                return address;
            }
        }
        return null;
    }

    private String fromBytes(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02X", bytes[i]))
                .collect(Collectors.joining("-"));
    }

    private boolean checkForTrustValidity(LocalDateTime trustDate) {
        LocalDateTime currentDate = LocalDateTime.now();
        return ChronoUnit.DAYS.between(trustDate, currentDate) <= trustValidityDays;
    }

    public User userAuthentication(User user, HttpServletRequest httpRequest, String fingerPrintInfo, String ipAddress, boolean isForgotPassword) {
        List<UserMfaDetails> mfaDetails = mfaRepository.findByUserId(user.getUserId());
        UserMfaDetails matchedMfa = null;
        boolean hasTrustedRecord = false;
        boolean hasUntrustedRecord = false;
        boolean forgotCheck = true;
        for (UserMfaDetails mfaProvider : mfaDetails) {
            if (Objects.equals(mfaProvider.getUserId(), user.getUserId())
                    && Objects.equals(ipAddress, mfaProvider.getIpAddress())
//                    && Objects.equals(fingerPrintInfo, mfaProvider.getFingerPrintInfo())
                    && encryptCode.matches(fingerPrintInfo, mfaProvider.getFingerPrintInfo())
            ) {
                if (mfaProvider.isTrusted() && checkForTrustValidity(mfaProvider.getRegisterDate())) {
                    hasTrustedRecord = true;
                    forgotCheck = false;
                    matchedMfa = mfaProvider;
                    break;
                } else {
                    matchedMfa = mfaProvider;
                    hasUntrustedRecord = true;
                }
            }
        }
        if (hasTrustedRecord && !isForgotPassword) {
            return user;
        }
        if (!hasUntrustedRecord) {
            hasUntrustedRecord = !forgotCheck;
        }
        return OTPNotification(user, hasUntrustedRecord, ipAddress, fingerPrintInfo, matchedMfa, isForgotPassword);
    }

    public User OTPNotification(User user, boolean hasUntrustedRecord, String ipAddress, String fingerPrintInfo, UserMfaDetails matchedMfa, boolean isForgotPassword) {
        OTPDetails mfaSecretCode = generateOTP(user.getEmailId());
        emailService.sendOtp(user.getEmailId(), mfaSecretCode.getOtp(), user.getUserName(), message);
        storeMfaDetails(mfaSecretCode, hasUntrustedRecord, user, ipAddress, fingerPrintInfo, matchedMfa, isForgotPassword);
        return new User();
    }

    public void storeMfaDetails(OTPDetails mfaSecretCode, boolean hasUntrustedRecord, User user, String ipAddress, String systemInfo, UserMfaDetails matchedMfa, boolean isForgotPassword) {
        if (!hasUntrustedRecord) {
            mfaProviderDao.saveMfaDetailsToDatabase(
                    user.getUserId(),
                    encryptCode.encode(mfaSecretCode.getOtp()),
                    ipAddress,
                    false,
                    mfaSecretCode.getTimestamp(),
//                    systemInfo
                    encryptCode.encode(systemInfo)
            );
        } else {
            mfaProviderDao.updateOtpAndIpAddressToDatabase(
                    matchedMfa.getId(),
                    user.getUserId(),
                    encryptCode.encode(mfaSecretCode.getOtp()),
                    ipAddress,
//                    systemInfo
                    encryptCode.encode(systemInfo)
            );
        }
    }

    public boolean validateOTP(String otp, int userId, boolean trustThisComputer, boolean isForgotPasswordOtp) {
        logger.info("forgot: {}", isForgotPasswordOtp);
        List<UserMfaDetails> mfaDetails = mfaRepository.findByUserId(userId);
        return mfaDetails.stream().anyMatch(mfaProvider -> {
            if (Objects.equals(mfaProvider.getUserId(), userId)
                    && encryptCode.matches(otp, mfaProvider.getMfaSecret())
                    && validateOTP(mfaProvider)) {
                if (trustThisComputer && !isForgotPasswordOtp) {
                    mfaProviderDao.updateMfaDetailsToDatabase(mfaProvider.getId(), true);
                }
                return true;
            }
            return false;
        });
    }
    
}

