package no.nav.melosys.eessi;

import no.nav.melosys.eessi.integration.dokkat.DokumenttypeIdConsumer;
import no.nav.melosys.eessi.integration.dokkat.DokumenttypeInfoConsumer;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.oppgave.OppgaveConsumer;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.util.SocketUtils;
import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackageClasses = MelosysEessiApplication.class )
public class ComponentTestConfig {
    static {
        System.setProperty("kafkaPort", String.valueOf(SocketUtils.findAvailableTcpPort(60000, 65535)));
    }
    
    @Bean
    @Order(1)
    EmbeddedKafkaBroker kafkaEmbedded(Environment env) {
        EmbeddedKafkaBroker kafka = new EmbeddedKafkaBroker(1, true, 1, 
                "privat-melosys-eessi-v1-local",
                "eessi-basis-sedMottatt-v1",
                "eessi-basis-sedSendt-v1");
        kafka.kafkaPorts(Integer.valueOf(env.getRequiredProperty("kafkaPort")));
        kafka.brokerProperty("offsets.topic.replication.factor", (short) 1);
        kafka.brokerProperty("transaction.state.log.replication.factor", (short) 1);
        kafka.brokerProperty("transaction.state.log.min.isr", 1);

        return kafka;
    }
    
    @Bean
    @Primary
    EuxConsumer euxConsumerMock() {
        return mock(EuxConsumer.class);
    }

    @Bean
    @Primary
    PersonConsumer personConsumerMock() {
        return mock(PersonConsumer.class);
    }

    @Bean
    @Primary
    AktoerConsumer aktoerConsumerMock() {
        return mock(AktoerConsumer.class);
        
    }

    @Bean
    @Primary
    PersonsokConsumer personsokConsumerMock() {
        return mock(PersonsokConsumer.class);
        
    }

    @Bean
    @Primary
    SakConsumer sakConsumerMock() {
        return mock(SakConsumer.class);
        
    }

    @Bean
    @Primary
    DokumenttypeIdConsumer dokumenttypeIdConsumerMock() {
        return mock(DokumenttypeIdConsumer.class);
    }
    
    @Bean
    @Primary
    DokumenttypeInfoConsumer dokumenttypeInfoConsumerMock() {
        return mock(DokumenttypeInfoConsumer.class);
    }

    @Bean
    @Primary
    JournalpostapiConsumer journalpostapiConsumerMock() {
        return mock(JournalpostapiConsumer.class);
    }

    @Bean
    @Primary
    OppgaveConsumer oppgaveConsumerMock() {
        return mock(OppgaveConsumer.class);
    }
}
