package no.nav.melosys.eessi.identifisering;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OppgaveEndretHendelse {
    private Long id;
    private String tildeltEnhetsnr;
    private String journalpostId;
    private String tilordnetRessurs;
    private String temagruppe;
    private String tema;
    private String behandlingstema;
    private String oppgavetype;
    private String behandlingstype;
    private Integer versjon;
    private String status;
    private String statuskategori;
    private String behandlesAvApplikasjon;
    private Ident ident;
    private Map<String, String> metadata;

    public boolean harMetadataRinasaksnummer() {
        return metadata.containsKey("RINA_SAKID");
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ident {
        private String identType;
        @ToString.Exclude
        private String verdi;
        @ToString.Exclude
        private String folkeregisterident;
    }

    @JsonIgnore
    public boolean erÅpen() {
        return "AAPEN".equalsIgnoreCase(statuskategori);
    }

    @JsonIgnore
    public boolean harAktørID() {
        return ident != null && "AKTOERID".equalsIgnoreCase(ident.identType) && ident.verdi != null;
    }

    @JsonIgnore
    public String hentAktørID() {
        return Optional.ofNullable(ident)
                .map(Ident::getVerdi)
                .orElseThrow(() -> new NoSuchElementException("Finner ikke aktørID"));
    }

    @JsonIgnore
    public boolean harSammeVersjon(int versjon ) {
        return this.versjon == versjon;
    }
}
