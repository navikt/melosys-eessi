package no.nav.melosys.eessi.integration.oppgave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpprettOppgaveResponseDto extends OppgaveDto {
    private String id;
}
