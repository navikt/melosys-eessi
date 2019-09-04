package no.nav.melosys.eessi.integration.gsak.oppgave;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OppgaveDto {
    private String id;
    private String aktoerId;
    private String tilordnetRessurs;
    private String tema;
    private String oppgavetype;
    private String journalpostId;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate aktivDato;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fristFerdigstillelse;
    private String prioritet;
    private String saksreferanse;
    private String behandlingstype;
    private String behandlingstema;
    private String temagruppe;
    private String tildeltEnhetsnr;
    private String behandlesAvApplikasjon;
    private String beskrivelse;
}
