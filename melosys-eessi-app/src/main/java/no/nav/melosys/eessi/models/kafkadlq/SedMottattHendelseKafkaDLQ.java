// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models.kafkadlq;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@DiscriminatorValue("SED_MOTTATT_HENDELSE")
public class SedMottattHendelseKafkaDLQ extends KafkaDLQ {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "melding", columnDefinition = "jsonb")
    private SedHendelse sedMottattHendelse;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sedMottattHendelse);
    }

    @java.lang.SuppressWarnings("all")
    public SedHendelse getSedMottattHendelse() {
        return this.sedMottattHendelse;
    }

    @java.lang.SuppressWarnings("all")
    public void setSedMottattHendelse(final SedHendelse sedMottattHendelse) {
        this.sedMottattHendelse = sedMottattHendelse;
    }

    @java.lang.SuppressWarnings("all")
    public SedMottattHendelseKafkaDLQ() {
    }
}
