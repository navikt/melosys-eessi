package no.nav.melosys.eessi.integration.tps.person;

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConsumerProducer {

    private PersonConsumerConfig consumerConfig;

    @Autowired
    public PersonConsumerProducer(PersonConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @Bean
    public PersonConsumer personConsumer() {
        PersonV3 port = consumerConfig.wrapWithSts(consumerConfig.getPort());
        return new PersonConsumer(port);
    }
}
