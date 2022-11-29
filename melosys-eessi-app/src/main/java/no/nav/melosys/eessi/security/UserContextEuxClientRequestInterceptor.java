package no.nav.melosys.eessi.security;

import no.nav.melosys.eessi.service.sts.RestStsClient;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class UserContextEuxClientRequestInterceptor extends UserContextClientRequestInterceptor {

    static final String CLIENT_NAME = "eux-rina-api";

    public UserContextEuxClientRequestInterceptor(RestStsClient restStsClient,
                                                  ClientConfigurationProperties clientConfigurationProperties,
                                                  OAuth2AccessTokenService oAuth2AccessTokenService) {
        super(restStsClient, clientConfigurationProperties, oAuth2AccessTokenService, CLIENT_NAME);
    }
}
