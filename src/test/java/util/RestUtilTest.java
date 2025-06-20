package util;

import com.amps.user.util.RestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
public class RestUtilTest {

    @InjectMocks
    RestUtil restUtil;

    Map<String,String> params = new HashMap<>();

    @Test
    void testPostMethod() {
        restUtil.postMethod("http://google.com", params);
    }
}
