package no.nav.melosys.eessi.service.sed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SedSendtMigreringRapportDto {
    private final String rinaSaksnummer;
    private final String dokumentId;
}
