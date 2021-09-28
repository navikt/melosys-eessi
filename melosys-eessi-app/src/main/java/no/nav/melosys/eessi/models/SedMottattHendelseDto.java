package no.nav.melosys.eessi.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedMottattHendelseDto {
    private Long id;
    private SedHendelse sedHendelse;
    private String journalpostId;
    private boolean publisertKafka;
    private LocalDateTime mottattDato;
    private LocalDateTime sistEndretDato;


    public Long getId() {
        return id;
    }

    public SedHendelse getSedHendelse() {
        return sedHendelse;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public boolean isPublisertKafka() {
        return publisertKafka;
    }

    public LocalDateTime getMottattDato() {
        return mottattDato;
    }

    public LocalDateTime getSistEndretDato() {
        return sistEndretDato;
    }

}
