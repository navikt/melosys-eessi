package no.nav.melosys.eessi.integration.oppgave;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OppgaveOppdateringDto {
    private final int id;
    private final int versjon;
    private final String status;
    private final String beskrivelse;
    private final String tildeltEnhetsnr;
    private final String tilordnetRessurs;

    private OppgaveOppdateringDto(Builder builder) {
        this.id = builder.id;
        this.versjon = builder.versjon;
        this.status = builder.status;
        this.beskrivelse = builder.beskrivelse;
        this.tildeltEnhetsnr = builder.tildeltEnhetsnr;
        this.tilordnetRessurs = builder.tilordnetRessurs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer versjon;
        private String status;
        private String beskrivelse;
        private String tildeltEnhetsnr;
        private String tilordnetRessurs;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder versjon(Integer versjon) {
            this.versjon = versjon;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder beskrivelse(String beskrivelse) {
            this.beskrivelse = beskrivelse;
            return this;
        }

        public Builder beskrivelse(String eksisterendeBeskrivelse, String tilleggsBeskrivelse) {
            this.beskrivelse = eksisterendeBeskrivelse + "\n\n" + tilleggsBeskrivelse;
            return this;
        }

        public Builder tildeltEnhetsnr(String tildeltEnhetsnr) {
            this.tildeltEnhetsnr = tildeltEnhetsnr;
            return this;
        }

        public Builder tilordnetRessurs(String tilordnetRessurs) {
            this.tilordnetRessurs = tilordnetRessurs;
            return this;
        }

        public OppgaveOppdateringDto build() {
            if (id == null) {
                throw new IllegalStateException("OppgaveID er påkrevd!");
            } else if (versjon == null) {
                throw new IllegalStateException("Versjon er påkrevd!");
            }
            return new OppgaveOppdateringDto(this);
        }
    }
}
