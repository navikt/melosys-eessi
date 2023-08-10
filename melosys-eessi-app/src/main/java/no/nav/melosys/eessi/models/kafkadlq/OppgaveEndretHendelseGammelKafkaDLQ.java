package no.nav.melosys.eessi.models.kafkadlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.melosys.eessi.identifisering.OppgaveEndretHendelseGammel;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("OPPGAVE_ENDRET_HENDELSE_GAMMEL")
public class OppgaveEndretHendelseGammelKafkaDLQ extends KafkaDLQ {

    @Type(type = "jsonb")
    @Column(name = "melding", columnDefinition = "jsonb")
    private OppgaveEndretHendelseGammel oppgaveEndretHendelseGammel;

    @Override
    public String hentMeldingSomStreng() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(oppgaveEndretHendelseGammel);
    }
}
