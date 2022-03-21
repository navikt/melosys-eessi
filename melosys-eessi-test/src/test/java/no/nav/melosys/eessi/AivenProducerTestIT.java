package no.nav.melosys.eessi;

import no.nav.melosys.eessi.kafka.producers.MelosysEessiAivenProducer;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.BucType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


class AivenProducerTestIT extends ComponentTestBase {

    @Autowired
    private MelosysEessiAivenProducer melosysEessiAivenProducer;

    @Test
    void melosysEessiMelding() {
        MelosysEessiMelding melosysEessiMelding = new MelosysEessiMelding();
        melosysEessiMelding.setBucType(BucType.LA_BUC_01.name());

        kafkaTestConsumer.reset(1);
        melosysEessiAivenProducer.publiserMelding(melosysEessiMelding);
        kafkaTestConsumer.doWait(5000);

        assertThat(hentMelosysEessiRecords()).hasSize(1);

    }
}
