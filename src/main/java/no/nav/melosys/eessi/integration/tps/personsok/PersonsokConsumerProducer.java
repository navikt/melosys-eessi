package no.nav.melosys.eessi.integration.tps.personsok;

import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonsokConsumerProducer {

    private PersonsokConsumerConfig consumerConfig;

    @Autowired
    public PersonsokConsumerProducer(PersonsokConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @Bean
    public PersonsokConsumer personsokConsumer() {
        PersonsokPortType port = consumerConfig.wrapWithSts(consumerConfig.getPort());
        return new PersonsokConsumer(port);
    }
}
