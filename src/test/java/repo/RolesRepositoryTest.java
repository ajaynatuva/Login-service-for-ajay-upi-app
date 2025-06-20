package repo;

import com.amps.user.model.Roles;
import com.amps.user.repository.RolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RolesRepository.class)
class RolesRepositoryTest {


    @MockBean
    RolesRepository rolesRepository;

    Roles testRole;

    @BeforeEach
    void setUp() {
        testRole = new Roles();
        testRole.setRoleId(1);
        testRole.setRoleName("Test Role");
        testRole.setRoleDesc("Test Role Description");
    }

    @Test
    void testFindByRoleId() {
        when(rolesRepository.findByRoleId(anyInt())).thenReturn(testRole);
        Roles foundRole = rolesRepository.findByRoleId(1);
        assertEquals(1, foundRole.getRoleId());
    }
}
