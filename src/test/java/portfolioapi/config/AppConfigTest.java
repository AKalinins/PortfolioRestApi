package portfolioapi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
class AppConfigTest {

    @Autowired
    private RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * {@link AppConfig#restClient()}
     */
    @Test
    void testRestClient() {
        assertNotNull(restClient);
    }

    /**
     * {@link AppConfig#objectMapper()}
     */
    @Test
    void testObjectMapper() {
        assertNotNull(objectMapper);
        assertFalse(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }
}
