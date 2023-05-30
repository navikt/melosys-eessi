package no.nav.melosys.eessi.models.kafkadlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorValue("SED_SENDT_HENDELSE")
public class SedSendtHendelseKafkaDLQ extends KafkaDLQ {

    @Type(type = "jsonb")
    @Column(name = "melding", columnDefinition = "jsonb")
    private SedHendelse sedSendtHendelse;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sedSendtHendelse);
    }
}
