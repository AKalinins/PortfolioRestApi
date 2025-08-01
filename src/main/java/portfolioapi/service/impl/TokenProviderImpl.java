package portfolioapi.service.impl;

import org.springframework.stereotype.Component;
import portfolioapi.service.TokenProvider;

@Component
public class TokenProviderImpl implements TokenProvider {

    @Override
    public String getToken() {
        return "";
    }
}
