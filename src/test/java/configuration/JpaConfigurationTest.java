package configuration;

import com.amps.user.configuration.JpaConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class JpaConfigurationTest {

    @InjectMocks
    JpaConfiguration jpaConfiguration;

    @Mock
    Environment environment;

    @Test
    void testDataSource(){
        jpaConfiguration.ipuDataSource();
    }
}
