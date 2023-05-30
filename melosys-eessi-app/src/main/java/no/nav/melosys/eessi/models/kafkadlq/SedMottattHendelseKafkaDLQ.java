package no.nav.melosys.eessi.models.kafkadlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@DiscriminatorValue("SED_MOTTATT_HENDELSE")
public class SedMottattHendelseKafkaDLQ extends KafkaDLQ {

    @Type(type = "jsonb")
    @Column(name = "melding", columnDefinition = "jsonb")
    private SedHendelse sedMottattHendelse;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sedMottattHendelse);
    }
}
