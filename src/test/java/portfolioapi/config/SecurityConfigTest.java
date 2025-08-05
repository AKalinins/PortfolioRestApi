package portfolioapi.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
class SecurityConfigTest {

    @MockitoBean
    private DataSource dataSource;

    @Autowired
    private SecurityFilterChain securityFilterChain;
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testSecurityFilterChain() {
        assertNotNull(securityFilterChain);
    }

    @Test
    void testUserDetailsManager() {
        assertNotNull(userDetailsManager);
    }

    @Test
    void testPasswordEncoder() {
        assertNotNull(passwordEncoder);
    }
}