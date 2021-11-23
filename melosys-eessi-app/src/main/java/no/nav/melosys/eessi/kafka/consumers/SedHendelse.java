package no.nav.melosys.eessi.kafka.consumers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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
    @ToString.Exclude
    private String navBruker;

    // avsenderID har formatet <landkodeISO2>:<institusjonID>
    @JsonIgnore
    public String getLandkode() {
        return avsenderId.substring(0, 2);
    }

    @JsonIgnore
    public boolean erIkkeX100() { return !sedType.equals("X100");}

}
