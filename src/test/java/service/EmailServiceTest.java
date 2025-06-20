package service;

import com.amps.user.service.impl.EmailService;
import com.amps.user.util.RestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static constants.ParameterConstants.ENVIRONMENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    RestUtil restUtil;

    @Test
    void testSendOTP(){
        ReflectionTestUtils.setField(emailService,ENVIRONMENT,"dev");
        String toEmail = "test@example.com";
        String otp = "12345";
        String userName = "testUser";
        String message = "test message";
        doNothing().when(restUtil).postMethod(anyString(),any());
        emailService.sendOtp(toEmail, otp, userName, message);
    }


}
