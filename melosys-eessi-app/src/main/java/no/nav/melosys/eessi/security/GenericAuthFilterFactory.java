package no.nav.melosys.eessi.security;


import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class GenericAuthFilterFactory {

    ClientConfigurationProperties clientConfigurationProperties;
    OAuth2AccessTokenService oAuth2AccessTokenService;

    public GenericAuthFilterFactory(ClientConfigurationProperties clientConfigurationProperties, OAuth2AccessTokenService oAuth2AccessTokenService) {
        this.clientConfigurationProperties = clientConfigurationProperties;
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
    }

    public GenericContextExchangeFilter getAzureFilter(String clientName) {
        return new AzureContextExchangeFilter(
            clientConfigurationProperties,
            oAuth2AccessTokenService,
            clientName);
    }
}
