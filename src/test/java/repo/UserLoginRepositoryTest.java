package repo;

import com.amps.user.IpuUserApplication;
import com.amps.user.model.User;
import com.amps.user.repository.UserLoginRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = IpuUserApplication.class)
@Sql("/data-test.sql")
class UserLoginRepositoryTest {

    @Autowired
    UserLoginRepository userLoginRepository;



    User testUser = new User(1, "admin", "admin@example.com", null, null, "test",0, false);

    @Test
    void testGetExistingUsers() {
        List<User> existingUsers = userLoginRepository.getExistingUsers();
        assertNotEquals(0, existingUsers.size());
    }

    @Test
    void testFindUser() {
        List<User> foundUsers = userLoginRepository.findUser(testUser.getUserName(), testUser.getEmailId());
        assertEquals(foundUsers.getFirst().toString(),testUser.toString());
    }

    @Test
    void testValidateUser(){
        User validatedUser = userLoginRepository.validateUser(testUser.getEmailId(), testUser.getPassword());
        assertEquals(testUser.toString(), validatedUser.toString());
    }

    @Test
    void testUpdatePassword(){
        String newPassword = "testPassword";
        userLoginRepository.updatePassword(newPassword, testUser.getUserId());
        User updatedUser = userLoginRepository.findByEmailId(testUser.getEmailId());
        assertEquals(newPassword, updatedUser.getPassword());
    }

    @Test
    void testDeleteUser(){
        userLoginRepository.deleteUser(4);
        assertFalse(userLoginRepository.findById(4).isPresent());
    }

    @Test
    void testFindByEmailId(){
        User foundUser = userLoginRepository.findByEmailId(testUser.getEmailId());
        assertEquals(testUser.toString(), foundUser.toString());
    }

    @Test
    void testGetUserId(){
        int userId = userLoginRepository.getUserId(testUser.getEmailId());
        assertEquals(testUser.getUserId(), userId);
    }

}