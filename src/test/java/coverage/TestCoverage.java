package coverage;

import com.amps.user.dto.OTPDetails;
import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.Roles;
import com.amps.user.model.UserRoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCoverage {

    @Test
    void testLogInDTO() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUserId(1);
        dto.setUserName("test");
        dto.setEmailId("test@example.com");
        Roles roles = new Roles();
        roles.setRoleId(1);
        roles.setRoleDesc("test");
        roles.setRoleName("test");
        UserRoles userRoles = new UserRoles();
        userRoles.setUserRoleKey(1);
        OTPDetails otpDetails = new OTPDetails("123",123L);
        otpDetails.setOtp("1234");
        otpDetails.setTimestamp(1234L);
        assertNull(userRoles.getUserId());
        assertNull(userRoles.getRoleId());
        assertNotNull(userRoles.getUserRoleKey());
        assertNotNull(roles.getRoleId());
        assertNotNull(roles.getRoleName());
        assertNotNull(roles.getRoleDesc());
        assertNotNull(dto.toString());
        assertNotNull(otpDetails.toString());
    }
}
