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

    private OppgaveOppdateringDto(int id, int versjon, String status, String beskrivelse) {
        this.id = id;
        this.versjon = versjon;
        this.status = status;
        this.beskrivelse = beskrivelse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer versjon;
        private String status;
        private String beskrivelse;

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

        public OppgaveOppdateringDto build() {
            if (id == null) {
                throw new IllegalStateException("OppgaveID er påkrevd!");
            } else if (versjon == null) {
                throw new IllegalStateException("Versjon er påkrevd!");
            }
            return new OppgaveOppdateringDto(id, versjon, status, beskrivelse);
        }
    }
}
