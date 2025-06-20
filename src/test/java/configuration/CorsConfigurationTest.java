package configuration;

import com.amps.user.configuration.CorsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@ExtendWith(MockitoExtension.class)
public class CorsConfigurationTest {

    @InjectMocks
    CorsConfiguration corsConfiguration;

    @Test
    public void testCorsConfiguration() {
        corsConfiguration.addCorsMappings(new CorsRegistry());
    }
}
