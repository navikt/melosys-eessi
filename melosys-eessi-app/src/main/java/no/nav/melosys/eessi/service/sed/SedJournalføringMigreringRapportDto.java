package no.nav.melosys.eessi.service.sed;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SedJournalføringMigreringRapportDto {
    private final List<SedMottattMigreringRapportDto> sedMottattMigreringRapportDtoList;
    private final List<SedSendtMigreringRapportDto> sedSendtMigreringRapportDtoList;
    private int antallSedMottattHendelser;
    private int antallSedSjekket;
}
