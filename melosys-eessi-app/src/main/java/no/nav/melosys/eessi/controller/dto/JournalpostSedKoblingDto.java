package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nav.melosys.eessi.models.JournalpostSedKobling;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JournalpostSedKoblingDto {

    private String journalpostID;
    private String sedID;
    private String rinaSaksnummer;
    private String bucType;
    private String sedType;

    public JournalpostSedKoblingDto(JournalpostSedKobling fra) {
        this(fra.getJournalpostID(), fra.getSedId(), fra.getRinaSaksnummer(), fra.getBucType(), fra.getSedType());
    }
}