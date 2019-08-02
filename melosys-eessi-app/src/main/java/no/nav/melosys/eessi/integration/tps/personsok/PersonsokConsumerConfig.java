package no.nav.melosys.eessi.integration.tps.personsok;

import javax.xml.namespace.QName;
import no.nav.melosys.eessi.config.AppCredentials;
import no.nav.melosys.eessi.config.SoapConsumerConfig;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonsokConsumerConfig extends SoapConsumerConfig {

    private static final String PERSONSOK_V1_WSDL = "wsdl/no/nav/tjeneste/virksomhet/personsoek/v1/Personsoek.wsdl";
    private static final String PERSONSOK_V1_NAMESPACE = "http://nav.no/tjeneste/virksomhet/personsoek/v1/";
    private static final QName PERSONSOK_V1_SERVICE = new QName(PERSONSOK_V1_NAMESPACE, "PersonsokPortType");
    private static final QName PERSONSOK_V1_PORT = new QName(PERSONSOK_V1_NAMESPACE, "PersonsokPortTypePort");

    private String endpointUrl;

    public PersonsokConsumerConfig(AppCredentials appCredentials,
                                   @Value("${melosys.integrations.sts-url}") String stsUrl,
                                   @Value("${melosys.integrations.personsok-url}") String endpointUrl) {
        super(appCredentials, stsUrl);
        this.endpointUrl = endpointUrl;
    }

    PersonsokPortType getPort() {
        return createPort(
                PersonsokPortType.class,
                PERSONSOK_V1_WSDL,
                PERSONSOK_V1_SERVICE,
                PERSONSOK_V1_PORT,
                endpointUrl
        );
    }
}
