package controller;

import com.amps.user.controller.UserLoginController;
import com.amps.user.dao.MFAProviderDao;
import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.User;
import com.amps.user.repository.RolesRepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.repository.UserRolesRepository;
import com.amps.user.service.OTPService;
import com.amps.user.service.UserLoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static constants.ParameterConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginControllerTest {

    @InjectMocks
    UserLoginController userLoginController;

    @Mock
    MFAProviderDao mfaProviderDao;

    @Mock
    UserLoginRepository userLoginRepository;

    @Mock
    RolesRepository rolesRepository;

    @Mock
    UserRolesRepository userRolesRepository;

    @Mock
    UserLoginService userLoginService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OTPService otpService;

    User user = new User();
    List<User> users = new ArrayList<>();
    UserLoginDTO userLoginDTO = new UserLoginDTO();
    Map<String, String> input = new HashMap<>();

    @BeforeEach
    void setUp() {
        input.put(USER_NAME, "test");
        input.put(EMAIL, "test");
        input.put(PASSWORD, "test");
        input.put(EMAIL_ID, "test@example.com");
        input.put(BROWSER_DETAILS, "test");
        input.put(IP_ADDRESS, "127.0.0.1");
        input.put(UNIQUE_ID_GENERATED, "123");
        input.put(IS_FORGOT_PASSWORD_OTP, "true");
        input.put(OTP_VALUE, "12345");
        input.put(TRUST_THIS_COMPUTER, "true");
    }


    @Test
    void testGetUser() {
        doReturn(users).when(userLoginService).getExistingUsers();
        assertEquals(users, userLoginController.getUser());
    }

    @Test
    void testSaveUser() {
        doReturn(user).when(userLoginService).createUser(any(UserLoginDTO.class));
        doNothing().when(userLoginService).saveUserRoles(any(), any());
        assertTrue(userLoginController.saveUser(userLoginDTO));
    }

    @Test
    void testSaveUser1() {
        doReturn(null).when(userLoginService).createUser(any(UserLoginDTO.class));
        assertTrue(userLoginController.saveUser(userLoginDTO));
    }

    @Test
    void testSaveUser2() {
        doThrow(new RuntimeException()).when(userLoginService).createUser(any(UserLoginDTO.class));
        assertThrows(RuntimeException.class, () -> userLoginController.saveUser(userLoginDTO));
    }

    @Test
    void testGetRoles() {
        doReturn(new ArrayList<>()).when(rolesRepository).findAll();
        assertEquals(new ArrayList<>(), userLoginController.getRoles());
    }

    @Test
    void testGetUser1() {
        when(userLoginRepository.findUser(anyString(), anyString())).thenReturn(null);
        assertEquals(Collections.emptyList(), userLoginController.getUser(input));
    }

    @Test
    void testGetUser2() {
        users.add(user);
        when(userLoginRepository.findUser(anyString(), anyString())).thenReturn(users);
        assertNotEquals(Collections.emptyList(), userLoginController.getUser(input));
    }

    @Test
    void testUpdateUser() {
        when(userLoginService.updateUser(any(UserLoginDTO.class))).thenReturn(null);
        assertFalse(userLoginController.updateUser(userLoginDTO));
    }

    @Test
    void testUpdateUser1() {
        when(userLoginService.updateUser(any(UserLoginDTO.class))).thenReturn(user);
        assertTrue(userLoginController.updateUser(userLoginDTO));
    }

    @Test
    void testUpdateUserException() {
        when(userLoginService.updateUser(any(UserLoginDTO.class))).thenThrow(new RuntimeException());
        assertFalse(userLoginController.updateUser(userLoginDTO));
    }

    @Test
    void testUpdatePassword() {
        input.put(USER_ID, "1");
        assertEquals(200, userLoginController.updatePassword(input).getStatusCode().value());
    }

    @Test
    void testDeleteUserById() {
        assertEquals(200, userLoginController.deleteUserById(1).getStatusCode().value());
    }

    @Test
    void testGetUserById() {
        doReturn(Optional.of(user)).when(userLoginRepository).findById(anyInt());
        assertEquals(user, userLoginController.getUserById(1));
    }

    @Test
    void testGetRolesById() {
        Map<String, Integer> params = new HashMap<>();
        params.put(USER_ID, 1);
        List<Integer> roles = new ArrayList<>();
        doReturn(roles).when(userRolesRepository).findRolesById(anyInt());
        assertEquals(roles, userLoginController.getRolesById(params));
    }

    @Test
    void testGetUserRoleById() {
        doReturn(Collections.emptyList()).when(userRolesRepository).findRoleById(anyInt());
        assertEquals(Collections.emptyList(), userLoginController.getUserRoleById(1));
    }

    @Test
    void testUpdateData() {
        doNothing().when(userLoginService).deleteUser(anyInt());
        userLoginController.updateData(1);
    }

    @Test
    void testValidateUsers() throws Exception {
        user.setDeletedb(0);
        user.setMfaRequired(false);
        when(userLoginService.login(any(), any())).thenReturn(user);
        assertEquals(user, userLoginController.validateUsers(input, null));
    }

    @Test
    void testValidateUsers1() throws Exception {
        user.setDeletedb(0);
        user.setMfaRequired(true);
        when(userLoginService.login(any(), any())).thenReturn(user);
        when(otpService.userAuthentication(any(), any(), any(), any(), any(Boolean.class))).thenReturn(user);
        assertEquals(user, userLoginController.validateUsers(input, null));
    }

    @Test
    void testValidateUsers2() {
        user.setDeletedb(0);
        user.setMfaRequired(true);
        when(userLoginService.login(any(), any())).thenReturn(null);
        assertThrows(Exception.class, () -> userLoginController.validateUsers(input, null));
    }

    @Test
    void testValidateUsers3() {
        user.setDeletedb(1);
        user.setMfaRequired(true);
        when(userLoginService.login(any(), any())).thenReturn(user);
        assertThrows(Exception.class, () -> userLoginController.validateUsers(input, null));
    }

    @Test
    void testCheckForValidOTP() {
        user.setUserId(1);
        when(userLoginRepository.findByEmailId(any())).thenReturn(user);
        when(otpService.validateOTP(any(), anyInt(), any(Boolean.class), any(Boolean.class))).thenReturn(true);
        doNothing().when(mfaProviderDao).updateLastLogin(anyInt());
        assertEquals(user, userLoginController.checkForValidOTP(input, null).getBody());
    }

    @Test
    void testCheckForValidOTP1() {
        input.put(IS_FORGOT_PASSWORD_OTP, "false");
        input.put(TRUST_THIS_COMPUTER, "false");
        user.setUserId(1);
        when(userLoginService.login(any(), any())).thenReturn(user);
        when(otpService.validateOTP(any(), anyInt(), any(Boolean.class), any(Boolean.class))).thenReturn(false);
        userLoginController.checkForValidOTP(input,null);
    }

    @Test
    void resendOtp() {
        user.setUserId(1);
        when(userLoginRepository.findByEmailId(any())).thenReturn(user);
        when(otpService.userAuthentication(any(), any(), any(), any(), anyBoolean())).thenReturn(user);
        assertEquals(user, userLoginController.ResendOtp(input, null));
    }

    @Test
    void testGetVersion() {
        ReflectionTestUtils.setField(userLoginController, VERSION, "127.0.1");
        assertEquals("127.0.1", userLoginController.getConfigPOMVersion());
    }

    @Test
    void emailIsValidTest() {
        user.setEmailId("test@example.com");
        when(userLoginService.validateEmail(user.getEmailId())).thenReturn(true);
        ResponseEntity<Boolean> result = userLoginController.validateEmail(user.getEmailId());
        assertThat(ResponseEntity.ok(true))
                .as("Email is valid in controller")
                .isEqualTo(result);
    }

    @Test
    void emailIsInvalidTest() {
        when(userLoginService.validateEmail("")).thenReturn(false);
        ResponseEntity<Boolean> result = userLoginController.validateEmail("");
        assertThat(ResponseEntity.ok(false))
                .as("Email is invalid in controller")
                .isEqualTo(result);
    }

}
