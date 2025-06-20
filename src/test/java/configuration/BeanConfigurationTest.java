package configuration;

import com.amps.user.configuration.BeanConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BeanConfigurationTest {

    @InjectMocks
    BeanConfiguration beanConfiguration;


    @Test
    void asyncExecutor() {
        assertNotNull(beanConfiguration.asyncExecutor());
    }

    @Test
    void testTaskScheduler() {
        assertNotNull(beanConfiguration.taskScheduler());
    }

    @Test
    void testEncoder(){
       assertNotNull(beanConfiguration.encoder());
    }

}
