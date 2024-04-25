package no.nav.melosys.eessi.service.sed;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SedJournalføringMigreringRapportDto {
    private final HashMap<String, String> rinasaksnummerTilDokumentId;
    private int antallSedMottattHendelser;
    private int antallSedSjekket;
}
