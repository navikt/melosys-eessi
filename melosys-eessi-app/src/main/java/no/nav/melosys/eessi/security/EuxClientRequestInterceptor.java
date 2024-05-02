package no.nav.melosys.eessi.security;

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class EuxClientRequestInterceptor extends ClientRequestInterceptor {

    static final String CLIENT_NAME = "eux-rina-api";

    public EuxClientRequestInterceptor(ClientConfigurationProperties clientConfigurationProperties,
                                       OAuth2AccessTokenService oAuth2AccessTokenService) {
        super(clientConfigurationProperties, oAuth2AccessTokenService, CLIENT_NAME);
    }
}
