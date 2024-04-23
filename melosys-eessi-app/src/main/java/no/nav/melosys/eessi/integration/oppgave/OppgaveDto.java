package no.nav.melosys.eessi.integration.oppgave;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OppgaveDto {
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
    @ToString.Exclude
    private String beskrivelse;
    private Map<OppgaveMetadataKey, String> metadata = new EnumMap<>(OppgaveMetadataKey.class);
}
