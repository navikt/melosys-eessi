package no.nav.melosys.eessi.identifisering.oppgave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HentOppgaveDto extends OppgaveDto {
    private String id;
}
