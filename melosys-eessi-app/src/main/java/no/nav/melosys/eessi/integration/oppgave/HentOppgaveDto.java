package no.nav.melosys.eessi.integration.oppgave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HentOppgaveDto extends OppgaveDto {
    private String id;
    private String status;
    private int versjon;

    public boolean erÅpen() {
        return !"FERDIGSTILT".equalsIgnoreCase(status) && !"FEILREGISTRERT".equalsIgnoreCase(status);
    }
}
