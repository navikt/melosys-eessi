package no.nav.melosys.eessi.integration.tps.person;

import javax.xml.namespace.QName;
import no.nav.melosys.eessi.config.AppCredentials;
import no.nav.melosys.eessi.config.SoapConsumerConfig;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
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
        return createPort(
                PersonV3.class,
                PERSON_V3_WSDL,
                PERSON_V3_SERVICE,
                PERSON_V3_PORT,
                endpointUrl
        );
    }
}
