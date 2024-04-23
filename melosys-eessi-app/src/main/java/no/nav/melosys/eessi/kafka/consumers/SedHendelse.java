package no.nav.melosys.eessi.kafka.consumers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import no.nav.melosys.eessi.models.SedType;

import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class SedHendelse {

    private static final List<String> TRENGER_KONTROLL = Stream.of(SedType.X001, SedType.X006, SedType.X007, SedType.X008).map(Enum::name).toList();
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
    public String getInstitusjon() {
        return avsenderId;
    }

    @JsonIgnore
    public boolean erX100() {
        return sedType.equals(SedType.X100.name());
    }

    @JsonIgnore
    public boolean erASED() {
        return SedType.valueOf(sedType).erASED();
    }

    @JsonIgnore
    public boolean erXSedSomTrengerKontroll() {
        return TRENGER_KONTROLL.contains(sedType.toUpperCase());
    }

}
