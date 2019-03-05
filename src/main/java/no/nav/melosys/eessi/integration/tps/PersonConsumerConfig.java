package no.nav.melosys.eessi.integration.tps;

import javax.xml.namespace.QName;
import no.nav.melosys.eessi.config.AppCredentials;
import no.nav.melosys.eessi.config.SoapConsumerConfig;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConsumerConfig extends SoapConsumerConfig {

    private static final String PERSON_V3_WSDL = "wsdl/no/nav/tjeneste/virksomhet/person/v3/Binding.wsdl";
    private static final String PERSON_V3_NAMESPACE = "http://nav.no/tjeneste/virksomhet/person/v3/Binding";
    private static final QName PERSON_V3_SERVICE = new QName(PERSON_V3_NAMESPACE, "Person_v3");
    private static final QName PERSON_V3_PORT = new QName(PERSON_V3_NAMESPACE, "Person_v3Port");

    private String endpointUrl;

    @Autowired
    public PersonConsumerConfig(AppCredentials appCredentials,
                                @Value("${melosys.integrations.sts-url}") String stsUrl,
                                @Value("${melosys.integrations.person-url}") String endpointUrl) {
        super(appCredentials, stsUrl);
        this.endpointUrl = endpointUrl;
    }

    PersonV3 getPort() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(PERSON_V3_WSDL);
        factoryBean.setServiceName(PERSON_V3_SERVICE);
        factoryBean.setEndpointName(PERSON_V3_PORT);
        factoryBean.setServiceClass(PersonV3.class);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());

        return factoryBean.create(PersonV3.class);
    }
}
