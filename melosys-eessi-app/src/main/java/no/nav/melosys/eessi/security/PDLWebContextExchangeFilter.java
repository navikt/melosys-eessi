package no.nav.melosys.eessi.security;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.OAuth2GrantType;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class PDLWebContextExchangeFilter extends GenericContextExchangeFilter {

    private final ClientProperties clientPropertiesForSystem;

    public PDLWebContextExchangeFilter(ClientConfigurationProperties clientConfigurationProperties,
                                       OAuth2AccessTokenService oAuth2AccessTokenService) {
        super(clientConfigurationProperties, oAuth2AccessTokenService, "pdl-web", true);

        clientPropertiesForSystem = ClientProperties.builder()
            .tokenEndpointUrl(clientProperties.getTokenEndpointUrl())
            .scope(clientProperties.getScope())
            .authentication(clientProperties.getAuthentication())
            .grantType(OAuth2GrantType.CLIENT_CREDENTIALS)
            .build();
    }

    @Override
    protected String getSystemToken() {
        return "Bearer " + oAuth2AccessTokenService.getAccessToken(clientPropertiesForSystem).getAccessToken();
    }
}
