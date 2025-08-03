package portfolioapi.service.impl.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TokenDtoTest {

    /**
     * {@link TokenDto#isValid()}
     */
    @Test
    void shouldReturnTrueForValidToken() {

        TokenDto tokenDto = TokenDto.builder()
                .token("token")
                .tokenObtainedAt(System.currentTimeMillis() - 10_000)
                .expiresIn(600)
                .build();

        assertTrue(tokenDto.isValid());
    }

    /**
     * {@link TokenDto#isValid()}
     */
    @Test
    void shouldReturnFalseForExpiredToken() {

        TokenDto tokenDto = TokenDto.builder()
                .token("token")
                .tokenObtainedAt(System.currentTimeMillis() - 1_000_000)
                .expiresIn(600)
                .build();

        assertFalse(tokenDto.isValid());
    }

    /**
     * {@link TokenDto#isValid()}
     */
    @Test
    void shouldReturnFalseForNullToken() {

        TokenDto tokenDto = TokenDto.builder()
                .tokenObtainedAt(System.currentTimeMillis())
                .expiresIn(600)
                .build();

        assertFalse(tokenDto.isValid());
    }

    /**
     * {@link TokenDto#isRefreshable()}
     */
    @Test
    void shouldReturnTrueForRefreshableToken() {

        TokenDto tokenDto = TokenDto.builder()
                .refreshToken("token")
                .tokenObtainedAt(System.currentTimeMillis() - 10_000)
                .refreshExpiresIn(1200)
                .build();

        assertTrue(tokenDto.isRefreshable());
    }

    /**
     * {@link TokenDto#isRefreshable()}
     */
    @Test
    void shouldReturnFalseForExpiredRefreshToken() {

        TokenDto tokenDto = TokenDto.builder()
                .refreshToken("token")
                .tokenObtainedAt(System.currentTimeMillis() - 1_000_000)
                .expiresIn(1200)
                .build();

        assertFalse(tokenDto.isRefreshable());
    }

    /**
     * {@link TokenDto#isRefreshable()}
     */
    @Test
    void shouldReturnFalseForNullRefreshToken() {

        TokenDto tokenDto = TokenDto.builder()
                .tokenObtainedAt(System.currentTimeMillis())
                .expiresIn(1200)
                .build();

        assertFalse(tokenDto.isRefreshable());
    }
}