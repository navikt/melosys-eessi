package no.nav.melosys.eessi.config;

import no.nav.melosys.eessi.security.StsConfigUtil;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppCredentials.class)
public abstract class SoapConsumerConfig {

    private String stsUrl;

    private AppCredentials appCredentials;

    public SoapConsumerConfig(AppCredentials appCredentials, String stsUrl) {
        this.appCredentials = appCredentials;
        this.stsUrl = stsUrl;
    }

    @Bean
    public LoggingOutInterceptor loggingOutInterceptor() {
        return new LoggingOutInterceptor();
    }

    @Bean
    public LoggingInInterceptor loggingInInterceptor() {
        return new LoggingInInterceptor();
    }

    public <T> T wrapWithSts(T port) {
        configureStsForSystemUser(port);
        Client client = ClientProxy.getClient(port);
        setClientTimeout(client);

        return port;
    }

    private void configureStsForSystemUser(Object port) {
        Client client = ClientProxy.getClient(port);
        StsConfigUtil.configureStsRequestSamlToken(
                client, stsUrl, appCredentials.getUsername(), appCredentials.getPassword());
    }

    private void setClientTimeout(Client client) {
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

        long timeout = 60000L;
        httpClientPolicy.setConnectionTimeout(timeout);
        httpClientPolicy.setReceiveTimeout(timeout);
        conduit.setClient(httpClientPolicy);
    }
}
