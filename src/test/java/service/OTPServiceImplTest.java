package service;

import com.amps.user.dao.MFAProviderDao;
import com.amps.user.model.User;
import com.amps.user.model.UserMfaDetails;
import com.amps.user.repository.MFARepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.service.impl.EmailService;
import com.amps.user.service.impl.OTPServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OTPServiceImplTest {

    @InjectMocks
    @Spy
    OTPServiceImpl otpService;

    @Mock
    NamedParameterJdbcTemplate ipuNamedParameterJdbcTemplate;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MFAProviderDao mfaProviderDao;

    @Mock
    UserLoginRepository userLoginRepository;

    @Mock
    MFARepository mfaRepository;

    @Mock
    EmailService emailService;


    UserMfaDetails userMfaDetails;

    User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "trustValidityDays", 90);
        ReflectionTestUtils.setField(otpService, "otpValidity", 10L);
        userMfaDetails = new UserMfaDetails();
        userMfaDetails.setUserId(1);
        userMfaDetails.setTsTokenExpiration(LocalDateTime.now());
        userMfaDetails.setMfaSecret("test");
        userMfaDetails.setCreatedDate(LocalDateTime.now());
        userMfaDetails.setTrusted(false);
        userMfaDetails.setId(1);
        userMfaDetails.setRegisterDate(LocalDateTime.now());
        userMfaDetails.setIpAddress("107.3.11");
        user = new User(1, "test", "test", null, null, "test", 0, true);
    }

    @Test
    void testGenerateOTP() {
        assertNotNull(otpService.generateOTP(null));
    }

    @Test
    void testValidateOTPFalse() {
        UserMfaDetails user = new UserMfaDetails();
        user.setTsTokenExpiration(LocalDateTime.now().minusMinutes(15));
        assertFalse(otpService.validateOTP(user));
    }

    @Test
    void testGenerateDeviceByIdWithNonNullMac() throws Exception {
        InetAddress mockIp = mock(InetAddress.class);
        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);
        byte[] mockMacAddress = new byte[]{(byte) 0x00, (byte) 0x1A, (byte) 0x2B, (byte) 0x3C, (byte) 0x4D, (byte) 0x5E};

        try (
                MockedStatic<InetAddress> inetMock = mockStatic(InetAddress.class);
                MockedStatic<NetworkInterface> netMock = mockStatic(NetworkInterface.class)
        ) {
            inetMock.when(InetAddress::getLocalHost).thenReturn(mockIp);
            netMock.when(() -> NetworkInterface.getByInetAddress(mockIp)).thenReturn(mockNetworkInterface);
            assertNotNull(mockNetworkInterface, "Mocked NetworkInterface is null");
            when(mockNetworkInterface.getHardwareAddress()).thenReturn(mockMacAddress);

            String mockedMac = ReflectionTestUtils.invokeMethod(otpService, "fromBytes", mockMacAddress);

            assertNotNull(mockedMac);
            assertEquals("00-1A-2B-3C-4D-5E", mockedMac);

            String result = otpService.generateDeviceById();

            assertNotNull(result);
            assertTrue(result.contains(mockedMac));
            assertTrue(result.contains(mockIp.toString()));
        }
    }

    @Test
    void testGenerateIdThrows() {
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            mockedInetAddress.when(InetAddress::getLocalHost)
                    .thenThrow(new UnknownHostException("Mocked UnknownHostException"));

            assertNull(otpService.generateDeviceById());
        }

    }
    @Test
    void testMacIsNull() throws Exception {
        InetAddress mockIp = mock(InetAddress.class);
        NetworkInterface mockInterface = mock(NetworkInterface.class);

        try (
                MockedStatic<InetAddress> inetMock = mockStatic(InetAddress.class);
                MockedStatic<NetworkInterface> netMock = mockStatic(NetworkInterface.class)
        ) {
            inetMock.when(InetAddress::getLocalHost).thenReturn(mockIp);
            netMock.when(() -> NetworkInterface.getByInetAddress(mockIp)).thenReturn(mockInterface);

            assertNotNull(mockInterface, "Mocked NetworkInterface is null");

            when(mockInterface.getHardwareAddress()).thenReturn(null);
            netMock.when(NetworkInterface::getNetworkInterfaces).thenReturn(Collections.emptyEnumeration());

            String result = otpService.generateDeviceById();

            assertNotNull(result);
            assertTrue(result.endsWith(mockIp.toString()));
        }
    }

    @Test
    void testFindMACAddressFromAllInterfaces() throws Exception {
        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);
        byte[] mockMacAddress = new byte[]{(byte) 0x00, (byte) 0x1A, (byte) 0x2B, (byte) 0x3C, (byte) 0x4D, (byte) 0x5E};

        when(mockNetworkInterface.getHardwareAddress()).thenReturn(mockMacAddress);

        Enumeration<NetworkInterface> mockNetworkInterfaces = Collections.enumeration(List.of(mockNetworkInterface));

        try (MockedStatic<NetworkInterface> netMock = mockStatic(NetworkInterface.class)) {
            netMock.when(NetworkInterface::getNetworkInterfaces).thenReturn(mockNetworkInterfaces);

            String result = ReflectionTestUtils.invokeMethod(otpService, "findMACAddressFromAllInterfaces");

            assertNotNull(result);
            assertEquals("00-1A-2B-3C-4D-5E", result);
        }
    }

    @Test
    void testUserAuthentication() {
        user.setMfaRequired(false);
        assertNotNull(otpService.userAuthentication(user, null, null, null, false));
    }

    @Test
    void testUserAuthentication1() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = true;
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication2() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = true;
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication3() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = true;
        userMfaDetails.setCreatedDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setTrusted(true);
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setIpAddress("107.3.11");
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication4() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication5() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication6() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3";
        boolean isForgotPassword = false;
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication7() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setUserId(5);
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication8() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setUserId(5);
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(new UserMfaDetails()));
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication9() {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setUserId(5);
        userMfaDetails.setTrusted(true);
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testUserAuthentication10() throws Exception {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = true;
        userMfaDetails.setUserId(user.getUserId());
        userMfaDetails.setIpAddress(ipAddress);
        userMfaDetails.setTrusted(true);
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(1));
        Field field = userMfaDetails.getClass().getDeclaredField("fingerPrintInfo");
        field.setAccessible(true);
        field.set(userMfaDetails, "hashed_fingerprint");
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(eq(fingerPrintInfo), eq("hashed_fingerprint"))).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, true));
    }

    @Test
    void testUserAuthentication11() throws Exception {
        String fingerPrintInfo = "test";
        String ipAddress = "107.3.11";
        boolean isForgotPassword = false;
        userMfaDetails.setUserId(user.getUserId());
        userMfaDetails.setIpAddress(ipAddress);
        userMfaDetails.setTrusted(false);
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        Field field = userMfaDetails.getClass().getDeclaredField("fingerPrintInfo");
        field.setAccessible(true);
        field.set(userMfaDetails, "hashed_fingerprint");
        when(mfaRepository.findByUserId(any())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(eq(fingerPrintInfo), eq("hashed_fingerprint"))).thenReturn(true);
        assertNotNull(otpService.userAuthentication(user, null, fingerPrintInfo, ipAddress, isForgotPassword));
    }

    @Test
    void testValidateOTP1() {
        String otp = "12345";
        int userId = 1;
        boolean trustThisComputer = true;
        boolean isForgotPasswordOtp = false;
        when(mfaRepository.findByUserId(anyInt())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertTrue(otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp));
    }

    @Test
    void testValidateOTP2() {
        String otp = "12345";
        int userId = 1;
        boolean trustThisComputer = true;
        boolean isForgotPasswordOtp = false;
        userMfaDetails.setCreatedDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        when(mfaRepository.findByUserId(anyInt())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        assertFalse(otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp));
    }

    @Test
    void testValidateOTP3() {
        String otp = "12345";
        int userId = 1;
        boolean trustThisComputer = true;
        boolean isForgotPasswordOtp = false;
        userMfaDetails.setUserId(5);
        userMfaDetails.setCreatedDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        when(mfaRepository.findByUserId(anyInt())).thenReturn(List.of(userMfaDetails));
        assertFalse(otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp));
    }

    @Test
    void testValidateOTP4() {
        String otp = "12345";
        int userId = 1;
        boolean trustThisComputer = true;
        boolean isForgotPasswordOtp = true;
        userMfaDetails.setCreatedDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        when(mfaRepository.findByUserId(anyInt())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertTrue(otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp));
    }

    @Test
    void testValidateOTP5() {
        String otp = "12345";
        int userId = 1;
        boolean trustThisComputer = false;
        boolean isForgotPasswordOtp = true;
        userMfaDetails.setCreatedDate(LocalDateTime.now().minusDays(100));
        userMfaDetails.setRegisterDate(LocalDateTime.now().minusDays(100));
        when(mfaRepository.findByUserId(anyInt())).thenReturn(List.of(userMfaDetails));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        assertTrue(otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp));
    }
}
