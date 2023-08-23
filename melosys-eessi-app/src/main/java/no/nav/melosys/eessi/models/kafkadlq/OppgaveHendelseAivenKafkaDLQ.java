package no.nav.melosys.eessi.models.kafkadlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.melosys.eessi.identifisering.OppgaveKafkaAivenRecord;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("OPPGAVE_HENDELSE_AIVEN")
public class OppgaveHendelseAivenKafkaDLQ extends KafkaDLQ {

    @Type(type = "jsonb")
    @Column(name = "melding", columnDefinition = "jsonb")
    private OppgaveKafkaAivenRecord oppgaveEndretHendelse;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(oppgaveEndretHendelse);
    }
}
