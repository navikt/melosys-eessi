package no.nav.melosys.eessi.models.kafkadlq;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("SED_SENDT_HENDELSE")
public class SedSendtHendelseKafkaDLQ extends KafkaDLQ {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    private SedHendelse sedSendtHendelse;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sedSendtHendelse);
    }
}
