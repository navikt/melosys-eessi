package no.nav.melosys.eessi.config;

import javax.xml.namespace.QName;
import no.nav.melosys.eessi.security.StsConfigUtil;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

    protected <T> T createPort(Class<T> portClass,
                            String wsdlURL,
                            QName serviceName,
                            QName endpointName,
                            String endpointUrl) {

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(wsdlURL);
        factoryBean.setServiceName(serviceName);
        factoryBean.setEndpointName(endpointName);
        factoryBean.setServiceClass(portClass);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());

        return factoryBean.create(portClass);
    }
}
