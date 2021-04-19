package no.nav.melosys.eessi.kafka.consumers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SedHendelse {
    private long id;
    private String sedId;
    private String sektorKode;
    private String bucType;
    private String rinaSakId;
    private String avsenderId;
    private String avsenderNavn;
    private String mottakerId;
    private String mottakerNavn;
    private String rinaDokumentId;
    private String rinaDokumentVersjon;
    private String sedType;
    private String navBruker;

    // avsenderID har formatet <landkodeISO2>:<institusjonID>
    public String getLandkode() {
        return avsenderId.substring(0, 2);
    }
}
