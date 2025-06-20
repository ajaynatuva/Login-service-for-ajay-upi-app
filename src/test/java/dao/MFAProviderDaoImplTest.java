package dao;

import com.amps.user.dao.impl.MFAProviderDaoImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class MFAProviderDaoImplTest {

    @InjectMocks
    MFAProviderDaoImpl mfaProviderDao;

    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void testSaveMfaDetailsToDatabase(){
        int userId = 5;
        String mfaSecret = "test";
        String ipAddress = "122.10";
        boolean trustThisComputer = true;
        long tsTokenExpiration = 15L;
        String systemInfo = "mac";
        mfaProviderDao.saveMfaDetailsToDatabase(userId, mfaSecret, ipAddress, trustThisComputer, tsTokenExpiration, systemInfo);
    }

    @Test
    void testUpdateMfaDetailsToDatabase(){
        mfaProviderDao.updateMfaDetailsToDatabase(1,true);
    }

    @Test
    void testUpdateLastLogin(){
        mfaProviderDao.updateLastLogin(1);
    }

    @Test
    void testUpdateOtpAndIpAddressToDatabase(){
        int id = 1;
        int userId = 2;
        String mfaSecret = "test";
        String ipAddress = "122.10";
        String systemInfo = "mac";
        mfaProviderDao.updateOtpAndIpAddressToDatabase(id, userId, mfaSecret, ipAddress, systemInfo);
    }


}
