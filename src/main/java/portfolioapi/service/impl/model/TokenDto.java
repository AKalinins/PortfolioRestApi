package portfolioapi.service.impl.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class TokenDto {
    private String token;
    private String refreshToken;
    private int expiresIn;
    private int refreshExpiresIn;
    private long tokenObtainedAt;

    public boolean isValid() {
        return Objects.nonNull(token)
                && ((System.currentTimeMillis() - tokenObtainedAt) / 1000) < expiresIn;
    }

    public boolean isRefreshable() {
        return Objects.nonNull(refreshToken)
                && ((System.currentTimeMillis() - tokenObtainedAt) / 1000) < refreshExpiresIn;
    }
}
