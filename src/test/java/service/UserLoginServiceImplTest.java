package service;

import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.Roles;
import com.amps.user.model.User;
import com.amps.user.repository.RolesRepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.repository.UserRolesRepository;
import com.amps.user.service.impl.UserLoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static constants.ParameterConstants.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceImplTest {

    @InjectMocks
    UserLoginServiceImpl userLoginService;

    @Mock
    UserLoginRepository userLoginRepository;

    @Mock
    UserRolesRepository userRolesRepository;

    @Mock
    RolesRepository rolesRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    UserLoginDTO userLoginDTO = new UserLoginDTO();

    List<User> users = new ArrayList<>();

    User user = new User();


    @Test
    void testCreateUser() {
        userLoginDTO.setPassword(PASSWORD);
        userLoginDTO.setDeletedb(0);
        userLoginDTO.setRoleId(null);
        user = userLoginService.createUser(userLoginDTO);
        assertNotEquals(PASSWORD, user.getPassword());
    }

    @Test
    void testCreateUserThrow() {
        doThrow(new RuntimeException()).when(userLoginRepository).save(user);
        User user1 = userLoginService.createUser(userLoginDTO);
        assertNull(user1);
    }

    @Test
    void testSaveUserRoles() {
        Roles roles = new Roles();
        roles.setRoleId(1);
        doNothing().when(userRolesRepository).deleteRoleById(any());
        doReturn(roles).when(rolesRepository).findByRoleId(any());
        User user = new User();
        List<Integer> rolesId = new ArrayList<>();
        rolesId.add(1);
        user.setUserId(0);
        userLoginService.saveUserRoles(user, rolesId);
        assertEquals(0, user.getUserId());
    }

    @Test
    void testSearchUser() {
        doReturn(users).when(userLoginRepository).findUser(anyString(), anyString());
        assertEquals(users, userLoginService.searchUser("test", "test@example.com"));
    }

    @Test
    void testUpdateUser() {
        doNothing().when(userLoginRepository).saveUser(anyString(), anyString(), any());
        user = userLoginService.updateUser(userLoginDTO);
        assertNull(user);
    }

    @Test
    void testUpdateUser1() {
        user = userLoginService.updateUser(userLoginDTO);
        assertNotNull(user);
    }

    @Test
    void testLogin() {
        User user1 = User.createEmptyUser();
        doReturn(user1).when(userLoginRepository).findByEmailId(anyString());
        doReturn(true).when(passwordEncoder).matches(any(), any());
        assertNotNull(userLoginService.login("test@example.com", "test"));
    }

    @Test
    void testLogin2() {
        doReturn(null).when(userLoginRepository).findByEmailId(anyString());
        assertNull(userLoginService.login("test@example.com", "test"));
    }

    @Test
    void testLogin3() {
        doReturn(user).when(userLoginRepository).findByEmailId(anyString());
        doReturn(false).when(passwordEncoder).matches(any(), any());
        assertNull(userLoginService.login("test@example.com", "test"));
    }

    @Test
    void testDeleteUser() {
        userLoginService.deleteUser(0);
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(), (MapSqlParameterSource) any());
    }

    @Test
    void testGetExistingUsers() {
        doReturn(users).when(userLoginRepository).getExistingUsers();
        assertEquals(users, userLoginService.getExistingUsers());
    }

    @Test
    void validateEmailTest() {

        User user = new User.Builder()
                .userId(66)
                .userName("shasank")
                .password("Shasank@123")
                .emailId("ssharma@amps.com")
                .deletedb(0)
                .createdOn(new Date())
                .lastLogin(new Date())
                .isMfaRequired(true)
                .build();
        when(userLoginRepository.findByEmailId(user.getEmailId()))
                .thenReturn(user);

        when(userLoginRepository.findByEmailId(""))
                .thenReturn(null);
        Boolean isValid = userLoginService.validateEmail(user.getEmailId());

        Boolean isValidAgainst = userLoginService.validateEmail("");
        assertThat(isValid)
                .as("Email is valid in service")
                .isTrue();
        assertThat(isValidAgainst)
                .as("Email is not valid in service")
                .isFalse();
    }

}
